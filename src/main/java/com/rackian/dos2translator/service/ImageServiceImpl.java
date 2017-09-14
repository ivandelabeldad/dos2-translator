package com.rackian.dos2translator.service;

import com.rackian.dos2translator.model.CurrentImage;
import com.rackian.dos2translator.model.PreviousImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class ImageServiceImpl implements ImageService {

    private VisionAPI visionAPI;
    private TranslationAPI translationAPI;
    private DialogTextMapperService dialogTextMapperService;
    private ImageGeneratorService imageGeneratorService;
    private CurrentImage currentImage;
    private PreviousImage previousImage;
    private ChangeDetector changeDetector;

    @Autowired
    public ImageServiceImpl(
            VisionAPI visionAPI,
            TranslationAPI translationAPI,
            DialogTextMapperService dialogTextMapperService,
            ImageGeneratorService imageGeneratorService,
            CurrentImage currentImage,
            PreviousImage previousImage,
            ChangeDetector changeDetector) {
        this.visionAPI = visionAPI;
        this.translationAPI = translationAPI;
        this.dialogTextMapperService = dialogTextMapperService;
        this.imageGeneratorService = imageGeneratorService;
        this.currentImage = currentImage;
        this.previousImage = previousImage;
        this.changeDetector = changeDetector;
    }

    @PostConstruct
    public void init() {
        previousImage.setImagePack(imageGeneratorService.createImagePack());
        currentImage.setImagePack(imageGeneratorService.createImagePack());
    }

    @Scheduled(fixedRate = 500)
    public void checkChanges() {
        if (changeDetector.changed()) {
            visionAPI.obtainText();
            translationAPI.translate();
        }
        if (!changeDetector.isTextBox()) {
            dialogTextMapperService.clearDialogs();
        }
    }

}
