package com.springboottest.base;

import com.springboottest.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;

/**

 */
public class ParamsValidHandler {

    private ValidationUtils validationUtils;

    private static Logger logger = LoggerFactory.getLogger(com.springboottest.base.ParamsValidHandler.class);

    /**
     * 私有构造方法
     */
    private ParamsValidHandler() {
    }

    private ParamsValidHandler(ValidationUtils validationUtils) {
        this.validationUtils = validationUtils;
    }

    /**
     * 创建参数校验器
     *
     * @param validationUtils
     * @return
     */
    public static com.springboottest.base.ParamsValidHandler create(ValidationUtils validationUtils) {
        com.springboottest.base.ParamsValidHandler paramsValidHandler = new com.springboottest.base.ParamsValidHandler(validationUtils);
        return paramsValidHandler;
    }

    /**
     * 校验异常信息
     *
     * @param result
     * @return
     * @throws ValidationException
     */
    public com.springboottest.base.ParamsValidHandler handle(BindingResult result) throws ValidationException {

        validationUtils.checkFieldError(result);

        return this;
    }
}
