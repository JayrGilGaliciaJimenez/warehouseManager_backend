package utez.edu.mx.warehousemanager.mapper;

import utez.edu.mx.warehousemanager.dto.RoleDto;
import utez.edu.mx.warehousemanager.dto.UserDto;
import utez.edu.mx.warehousemanager.model.RoleModel;
import utez.edu.mx.warehousemanager.model.UserModel;

public class UserMapper {
    private UserMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static RoleDto toRoleDto(RoleModel role) {
        if (role == null)
            return null;

        return new RoleDto(
                role.getId(),
                role.getName());
    }

    public static UserDto toUserDto(UserModel user) {
        if (user == null)
            return null;

        return new UserDto(
                user.getId(),
                user.getUuid(),
                user.getName(),
                user.getLastname(),
                user.getEmail(),
                user.getCreationDate(),
                user.getStatus(),
                toRoleDto(user.getRole()));
    }

    public static RoleModel toRoleModel(RoleDto roleDto) {
        if (roleDto == null) {
            return null;
        }

        RoleModel roleModel = new RoleModel();
        roleModel.setId(roleDto.getId());
        roleModel.setName(roleDto.getName());
        return roleModel;
    }
}
