package com.ayanlabs.core.service;

import com.ayanlabs.core.data.CoreContext;
import com.ayanlabs.core.entity.UserType;

import java.util.List;

public interface IUserTypeService extends IBaseService<UserType> {

    List<UserType> findAll(CoreContext context);
}

