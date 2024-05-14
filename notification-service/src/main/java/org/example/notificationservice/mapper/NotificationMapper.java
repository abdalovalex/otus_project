package org.example.notificationservice.mapper;

import org.example.notificationservice.dto.NotificationDto;
import org.example.notificationservice.entity.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface NotificationMapper {
    @Mapping(target = ".", source = "notification")
    NotificationDto map(Notification notification);
}
