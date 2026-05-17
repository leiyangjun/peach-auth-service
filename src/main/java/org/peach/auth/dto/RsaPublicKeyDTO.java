package org.peach.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 登录口令 RSA 公钥下发（前端加密口令用）。
 *
 * @author leiyangjun
 */
@Data
@Schema(description = "RSA 公钥（用于加密口令）")
public class RsaPublicKeyDTO {

	@Schema(description = "算法", example = "RSA")
	private String algorithm;

	@Schema(description = "填充", example = "RSA_PKCS1_V1_5")
	private String padding;

	@Schema(description = "密钥位数", example = "2048")
	private int keyBits;

	@Schema(description = "SPKI PEM")
	private String publicKeyPem;
}
