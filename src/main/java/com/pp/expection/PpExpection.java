package com.pp.expection;

import com.pp.common.constants.CommonError;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PpExpection extends RuntimeException {
    private CommonError error;


    private String msg;
    private Integer code;
    private String status;

    public PpExpection(CommonError commonError) {
        this(commonError.getMessage(), commonError.getCode(), commonError.getStatus());
    }

    public PpExpection(String msg, Integer code, String status) {
        this.msg = msg;
        this.code = code;
        this.status = status;
    }


    public static PpExpection newsPpExpection(CommonError error) {
        return new PpExpection(error);
    }


    public static PpExpection newsPpExpection(String msg, Integer code, String status) {
        return new PpExpection(msg, code, status);
    }

}
