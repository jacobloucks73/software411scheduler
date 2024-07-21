package com.psu.scheduler;


import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.GenerationConfig;
import com.google.cloud.vertexai.api.HarmCategory;
import com.google.cloud.vertexai.api.Part;
import com.google.cloud.vertexai.api.SafetySetting;
import com.google.cloud.vertexai.generativeai.ContentMaker;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseStream;
import java.io.IOException;
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
                    .setText("please give me multiple different formatting choices so i can see what they all are, please do: bold,italics,underline, and indentation. please label them all as well before you've done it" )
                    .build();

            var content = ContentMaker.fromMultiModalData(part);
            ResponseStream<GenerateContentResponse> responseStream = model.generateContentStream(content);
            StringBuilder fullText = new StringBuilder();
            // Do something with the response
            responseStream.stream().forEach(response -> {
                response.getCandidatesList().forEach(candidate -> {
                    candidate.getContent().getPartsList().forEach(partContent -> {
                        fullText.append(partContent.getText());
                    });

                });

            });
            //Start of postprocessing on the text


            System.out.println("Gen4" +
                    ".erated Text: " + fullText.toString());
        }
    }
}
