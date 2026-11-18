package com.ayanlab.core.service;

import com.ayanlab.core.data.CoreContext;
import com.ayanlab.core.entity.UserType;

import java.util.List;

public interface IUserTypeService extends IBaseService<UserType> {

    List<UserType> findAll(CoreContext context);
}

