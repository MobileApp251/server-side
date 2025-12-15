package mobile.jira.clonejira.enums.converter;

import jakarta.persistence.Converter;         // <--- CHỈNH SỬA Ở ĐÂY
import jakarta.persistence.AttributeConverter;

import mobile.jira.clonejira.enums.*;

// Trong file TaskStatusConverter.java
@Converter(autoApply = true) // Hoặc chỉ áp dụng thủ công
public class ProjectRoleConverter implements AttributeConverter<ProjectRole, String> {

    // Từ Enum (Java) sang DB (String)
    @Override
    public String convertToDatabaseColumn(ProjectRole status) {
        if (status == null) {
            return null;
        }
        // Sử dụng giá trị chuỗi chữ thường mà bạn đã định nghĩa trong Enum
        return status.getValue(); 
    }

    // Từ DB (String) sang Enum (Java)
    @Override
    public ProjectRole convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        
        // Tìm kiếm hằng số Enum có giá trị chuỗi (value) khớp với dữ liệu DB
        for (ProjectRole status : ProjectRole.values()) {
            if (status.getValue().equalsIgnoreCase(dbData)) { 
                return status;
            }
        }
        
        throw new IllegalArgumentException(
            "Invalid ProjectRole value in database: " + dbData
        );
    }
}