package org.example.samplespringboot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadRequestDTO {
    private String fileName;
    private String fileType;
    private Long fileSize;
}

