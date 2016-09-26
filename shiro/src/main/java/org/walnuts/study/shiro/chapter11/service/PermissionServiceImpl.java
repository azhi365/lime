package org.walnuts.study.shiro.chapter11.service;

import org.walnuts.study.shiro.chapter11.dao.PermissionDao;
import org.walnuts.study.shiro.chapter11.dao.PermissionDaoImpl;
import org.walnuts.study.shiro.chapter11.entity.Permission;

/**
 * <p>User: Zhang Kaitao
 * <p>Date: 14-1-28
 * <p>Version: 1.0
 */
public class PermissionServiceImpl implements PermissionService {

    private PermissionDao permissionDao = new PermissionDaoImpl();

    public Permission createPermission(Permission permission) {
        return permissionDao.createPermission(permission);
    }

    public void deletePermission(Long permissionId) {
        permissionDao.deletePermission(permissionId);
    }
}
