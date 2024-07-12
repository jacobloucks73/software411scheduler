package com.psu.scheduler;
/*/
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.aiplatform.v1.PredictionServiceClient;
import com.google.cloud.aiplatform.v1.PredictRequest;
import com.google.cloud.aiplatform.v1.PredictResponse;
import com.google.cloud.aiplatform.v1.EndpointName;
import com.google.protobuf.Value;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

public class GeminiAPI {
    public static void main(String[] args) {
        try {
            // Use the direct file path to the credentials JSON
            String credentialsPath = "C:\\Users\\FBIVAN2\\Downloads\\SWENG411Project\\demo\\src\\main\\resources\\taskscheduler-429105-fcf0e597b021.json";
            System.out.println("Using credentials file: " + credentialsPath);

            // Initialize credentials
            GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(credentialsPath));
            System.out.println("Credentials successfully initialized.");

            // Initialize the Vertex AI client
            PredictionServiceClient predictionServiceClient = PredictionServiceClient.create();
            System.out.println("Prediction service client successfully initialized.");

            // Set the endpoint name and request
            String projectId = "taskscheduler-429105";
            String location = "us-cent ral1";
            String endpointId = "https://us-east1-aiplatform.googleapis.com";
            EndpointName endpointName = EndpointName.of(projectId, location, endpointId);

            // Create an instance
            Value instanceValue = Value.newBuilder().setStringValue("Hello, Gemini!").build();

            // Create the request
            PredictRequest predictRequest = PredictRequest.newBuilder()
                    .setEndpoint(endpointName.toString())
                    .addAllInstances(Arrays.asList(instanceValue))
                    .build();

            // Make the prediction
            PredictResponse response = predictionServiceClient.predict(predictRequest);
            System.out.println("Prediction response: " + response);

            // Close the client
            predictionServiceClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
/*/

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.Blob;
import com.google.cloud.vertexai.api.Content;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.GenerationConfig;
import com.google.cloud.vertexai.api.HarmCategory;
import com.google.cloud.vertexai.api.Part;
import com.google.cloud.vertexai.api.SafetySetting;
import com.google.cloud.vertexai.generativeai.ContentMaker;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.PartMaker;
import com.google.cloud.vertexai.generativeai.ResponseStream;
import com.google.protobuf.ByteString;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GeminiAPI {
    public static void main(String[] args) throws IOException {
        try (VertexAI vertexAi = new VertexAI("taskscheduler-429105", "us-central1"); ) {
            GenerationConfig generationConfig =
                    GenerationConfig.newBuilder()
                            .setMaxOutputTokens(8192)
                            .setTemperature(1F)
                            .setTopP(0.95F)
                            .build();
            List<SafetySetting> safetySettings = Arrays.asList(
                    SafetySetting.newBuilder()
                            .setCategory(HarmCategory.HARM_CATEGORY_HATE_SPEECH)
                            .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_MEDIUM_AND_ABOVE)
                            .build(),
                    SafetySetting.newBuilder()
                            .setCategory(HarmCategory.HARM_CATEGORY_DANGEROUS_CONTENT)
                            .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_MEDIUM_AND_ABOVE)
                            .build(),
                    SafetySetting.newBuilder()
                            .setCategory(HarmCategory.HARM_CATEGORY_SEXUALLY_EXPLICIT)
                            .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_MEDIUM_AND_ABOVE)
                            .build(),
                    SafetySetting.newBuilder()
                            .setCategory(HarmCategory.HARM_CATEGORY_HARASSMENT)
                            .setThreshold(SafetySetting.HarmBlockThreshold.BLOCK_MEDIUM_AND_ABOVE)
                            .build()
            );
            GenerativeModel model =
                    new GenerativeModel.Builder()
                            .setModelName("gemini-1.5-flash-001")
                            .setVertexAi(vertexAi)
                            .setGenerationConfig(generationConfig)
                            .setSafetySettings(safetySettings)
                            .build();

            // Create parts for the prompt input
            Part part = Part.newBuilder()
                    .setText("This is a sample prompt text.")
                    .build();

            var content = ContentMaker.fromMultiModalData(part);
            ResponseStream<GenerateContentResponse> responseStream = model.generateContentStream(content);

            // Do something with the response
            responseStream.stream().forEach(response -> {
                response.getCandidatesList().forEach(candidate -> {
                    StringBuilder fullText = new StringBuilder();
                    candidate.getContent().getPartsList().forEach(partContent -> {
                        fullText.append(partContent.getText());
                    });
                    System.out.println("Generated Text: " + fullText.toString());
                });
            });
        }
    }
}
