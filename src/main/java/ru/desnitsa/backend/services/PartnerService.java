package ru.desnitsa.backend.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.desnitsa.backend.dto.PartnerDto;
import ru.desnitsa.backend.entities.Partner;
import ru.desnitsa.backend.exceptions.ContentNotFoundException;
import ru.desnitsa.backend.exceptions.IOBadRequestException;
import ru.desnitsa.backend.repositories.PartnerRepository;
import ru.desnitsa.backend.utils.ImageUtil;

import java.util.Optional;

@Service
public class PartnerService {

    private final PartnerRepository partnerRepository;
    private final ImageService imageService;
    private final ImageUtil imageUtil;

    public PartnerService(PartnerRepository repository, ImageService imageService, ImageUtil imageUtil) {
        this.partnerRepository = repository;
        this.imageService = imageService;
        this.imageUtil = imageUtil;
    }

    public Iterable<Partner> getAllPartners() {
        return partnerRepository.findAll();
    }

    public Partner getPartner(Long id) {
        return partnerRepository.findById(id).orElseThrow(ContentNotFoundException::new);
    }

    public Partner savePartner(PartnerDto partnerDto) {
        checkPartnerDto(partnerDto);
        String imageUrl = imageUtil.resizeAndSaveFile(partnerDto.imageFile());
        return partnerRepository.save(Partner.of(partnerDto.title(), imageUrl, partnerDto.link()));
    }

    @Transactional
    public Partner updatePartner(Long id, PartnerDto partnerDto) {
        checkPartnerDto(partnerDto);
        Partner existingPartner = partnerRepository.findById(id).orElseThrow(ContentNotFoundException::new);
        String imageUrl = imageUtil.resizeAndSaveFile(partnerDto.imageFile());
        Partner partner = new Partner(existingPartner.id(), partnerDto.title(), imageUrl, partnerDto.link());
        imageService.deleteImage(existingPartner.imageUrl());
        return partnerRepository.save(partner);
    }

    public void deletePartner(Long id) {
        String imageUrl;
        Optional<Partner> optionalPartner = partnerRepository.findById(id);
        if (optionalPartner.isPresent()) {
            imageUrl = optionalPartner.get().imageUrl();
            if (imageUrl != null) {
                imageService.deleteImage(imageUrl.substring(imageUrl.lastIndexOf("/") + 1));
            }
            partnerRepository.deleteById(id);
        }
    }

    private void checkPartnerDto(PartnerDto partnerDto) {
        if (partnerDto == null || partnerDto.title() == null || partnerDto.title().isEmpty() || partnerDto.title().isBlank()) {
            throw new IOBadRequestException();
        }
    }
}
