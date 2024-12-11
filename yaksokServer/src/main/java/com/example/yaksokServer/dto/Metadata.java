package com.example.yaksokServer.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Metadata {
    private String shape;       /* 낱알 형태 */
    private String drug_code;   /* 약정원코드 문자열, 사용 안함 */

    public Metadata() {
        shape = "none";
        drug_code = "none";
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("\t");
        buffer.append("shape");
        buffer.append("\t");
        buffer.append("drug_code");

        buffer.append("\n");

        buffer.append("0");
        buffer.append("\t");
        buffer.append(shape);
        buffer.append("\t");
        buffer.append(drug_code);

        return buffer.toString();
    }

}

