package com.pp.domain.service.api;

import org.springframework.web.multipart.MultipartFile;

public interface CsvService {
    Boolean parseCaseAndModule(MultipartFile file, Long unitId);
}
