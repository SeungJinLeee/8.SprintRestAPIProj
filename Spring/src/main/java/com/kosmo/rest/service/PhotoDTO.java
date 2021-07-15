package com.kosmo.rest.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhotoDTO {
	private String imageUrl;
	private String text;
	
}
