package uz.coder.davomatbackend.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.coder.davomatbackend.db.*;
import uz.coder.davomatbackend.db.model.AttendanceDbModel;
import uz.coder.davomatbackend.db.model.CourseDbModel;
import uz.coder.davomatbackend.db.model.GroupDbModel;
import uz.coder.davomatbackend.db.model.StudentDbModel;
import uz.coder.davomatbackend.model.Attendance;
import uz.coder.davomatbackend.todo.Strings;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static uz.coder.davomatbackend.todo.Strings.*;

@Slf4j
@Service
public class AttendanceService {

    private final AttendanceDatabase attendanceDatabase;
    private final StudentDatabase studentDatabase;
    private final GroupDatabase groupDatabase;
    private final UserDatabase userDatabase;
    private final CourseDatabase courseDatabase;

    @Autowired
    public AttendanceService(AttendanceDatabase attendanceDatabase,
                             StudentDatabase studentDatabase,
                             GroupDatabase groupDatabase,
                             UserDatabase userDatabase,
                             CourseDatabase courseDatabase) {
        this.attendanceDatabase = attendanceDatabase;
        this.studentDatabase = studentDatabase;
        this.groupDatabase = groupDatabase;
        this.userDatabase = userDatabase;
        this.courseDatabase = courseDatabase;
    }

    public Attendance save(Attendance attendance) {
        Optional<AttendanceDbModel> existingOpt =
                attendanceDatabase.findByStudentIdAndDate(attendance.getStudentId(), attendance.getDate());

        if (existingOpt.isPresent()) {
            AttendanceDbModel existing = existingOpt.get();
            existing.setStatus(attendance.getStatus());
            AttendanceDbModel updated = attendanceDatabase.save(existing);
            return mapToDto(updated);
        } else {
            AttendanceDbModel saved = attendanceDatabase.save(new AttendanceDbModel(
                    attendance.getStudentId(), attendance.getDate(), attendance.getStatus()
            ));
            return mapToDto(saved);
        }
    }

    public boolean saveAllByExcel(MultipartFile file) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            Row header = sheet.getRow(0);

            List<AttendanceDbModel> saveList = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String phone = getCellStringValue(row.getCell(2));
                StudentDbModel student = studentDatabase.findAll().stream()
                        .filter(s -> s.getPhoneNumber().equals(phone))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException(THERE_IS_NO_SUCH_AN_ATTENDANCE));

                for (int j = 5; j < header.getLastCellNum(); j++) {
                    String dateString = getCellStringValue(header.getCell(j));
                    if (dateString.isEmpty()) continue;

                    LocalDate date;
                    try {
                        // Sana formatini parse qilish
                        date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    } catch (Exception e) {
                        log.warn("Invalid date format in header: {}", dateString);
                        continue;
                    }

                    String value = getCellStringValue(row.getCell(j)).trim();
                    if (value.isEmpty()) continue;

                    if (attendanceDatabase.findByStudentIdAndDate(student.getId(), date).isEmpty()) {
                        saveList.add(new AttendanceDbModel(student.getId(), date, value));
                    }
                }
            }

            attendanceDatabase.saveAll(saveList);
            return true;

        } catch (IOException e) {
            log.error("Excel import error", e);
            return false;
        }
    }

    /**
     * Universal metod: barcha cell turlarini stringga aylantiradi.
     * Sana bo'lsa, "yyyy-MM-dd" formatida qaytaradi.
     */
    public String getCellStringValue(Cell cell) {
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    Date date = cell.getDateCellValue();
                    LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    return localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                } else {
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == (long) numericValue) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                // Formulani hisoblab, natijani olish
                FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
                CellValue cellValue = evaluator.evaluate(cell);
                switch (cellValue.getCellType()) {
                    case STRING: return cellValue.getStringValue();
                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(cell)) {
                            Date date = cell.getDateCellValue();
                            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                            return localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        }
                        double numericValue = cellValue.getNumberValue();
                        if (numericValue == (long) numericValue) return String.valueOf((long) numericValue);
                        else return String.valueOf(numericValue);
                    case BOOLEAN: return String.valueOf(cellValue.getBooleanValue());
                    default: return "";
                }
            default:
                return "";
        }
    }

    // ------------------- Excel export by month -------------------
    public byte[] exportToExcelByMonth(long userId, Long courseId, Long groupId, int year, int month) throws IOException {

        List<StudentDbModel> students = studentDatabase.findAllStudentsByOwnerUserId(userId);

        if (courseId != null) {
            students = students.stream()
                    .filter(s -> {
                        GroupDbModel g = groupDatabase.findById(s.getGroupId()).orElse(null);
                        return g != null && g.getCourseId() == courseId;
                    })
                    .toList();
        }
        if (groupId != null) {
            students = students.stream()
                    .filter(s -> s.getGroupId() == groupId)
                    .toList();
        }

        Map<Long, String> studentNames = new HashMap<>();
        for (StudentDbModel student : students) {
            userDatabase.findById(student.getUserId())
                    .ifPresent(user -> studentNames.put(
                            student.getId(),
                            user.getFirstName() + " " + user.getLastName()
                    ));
        }

        List<AttendanceDbModel> attendanceList =
                attendanceDatabase.findAllByTeacherAndOptionalCourseAndGroup(userId, courseId, groupId);

        Set<LocalDate> targetDates = attendanceList.stream()
                .map(AttendanceDbModel::getDate)
                .filter(date -> date.getYear() == year && date.getMonthValue() == month)
                .collect(Collectors.toCollection(TreeSet::new));

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Attendance");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue(Strings.NUMBER);
            header.createCell(1).setCellValue(Strings.FULL_NAME);
            header.createCell(2).setCellValue(Strings.PHONE);
            header.createCell(3).setCellValue(Strings.COURSE);
            header.createCell(4).setCellValue(Strings.GROUP);

            int cellIndex = 5;
            for (LocalDate date : targetDates) {
                header.createCell(cellIndex++).setCellValue(date.toString());
            }

            int rowNum = 1;
            for (StudentDbModel student : students) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(rowNum - 1);
                row.createCell(1).setCellValue(studentNames.getOrDefault(student.getId(), ""));
                row.createCell(2).setCellValue(student.getPhoneNumber());

                GroupDbModel group = groupDatabase.findById(student.getGroupId())
                        .orElseThrow(() -> new IllegalArgumentException(Strings.THERE_IS_NO_SUCH_A_GROUP));

                CourseDbModel course = courseDatabase.findById(group.getCourseId())
                        .orElseThrow(() -> new IllegalArgumentException(Strings.THERE_IS_NO_SUCH_A_COURSE));

                row.createCell(3).setCellValue(course.getTitle());
                row.createCell(4).setCellValue(group.getTitle());

                Map<LocalDate, String> attMap = attendanceList.stream()
                        .filter(a -> a.getStudentId().equals(student.getId()))
                        .collect(Collectors.toMap(
                                AttendanceDbModel::getDate,
                                AttendanceDbModel::getStatus,
                                (a, b) -> a
                        ));

                int colIndex = 5;
                for (LocalDate date : targetDates) {
                    String status = attMap.get(date);
                    row.createCell(colIndex++).setCellValue(status != null ? status : "");
                }
            }

            for (int i = 0; i < header.getLastCellNum(); i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }

    public List<Attendance> getAllByStudentId(long studentId) {
        return attendanceDatabase.findAllByStudentId(studentId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public Page<Attendance> getAllByStudentIdPaginated(long studentId, Pageable pageable) {
        Page<AttendanceDbModel> attendancePage = attendanceDatabase.findAllByStudentId(studentId, pageable);
        return attendancePage.map(this::mapToDto);
    }

    public Attendance update(Attendance updated) {
        AttendanceDbModel model = attendanceDatabase.findById(updated.getId())
                .orElseThrow(() -> new IllegalArgumentException(THERE_IS_NO_SUCH_AN_ATTENDANCE));

        model.setDate(updated.getDate());
        model.setStudentId(updated.getStudentId());
        model.setStatus(updated.getStatus());

        AttendanceDbModel saved = attendanceDatabase.save(model);
        return mapToDto(saved);
    }

    public boolean delete(long id) {
        if (!attendanceDatabase.existsById(id)) return false;
        attendanceDatabase.deleteById(id);
        return true;
    }

    private Attendance mapToDto(AttendanceDbModel dbModel) {
        if (dbModel == null) return null;
        return new Attendance(dbModel.getId(), dbModel.getStudentId(), dbModel.getDate(), dbModel.getStatus());
    }

    public Attendance findById(long id) {
        AttendanceDbModel attendanceDbModel = attendanceDatabase.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(THERE_IS_NO_SUCH_AN_ATTENDANCE));
        return mapToDto(attendanceDbModel);
    }
}
