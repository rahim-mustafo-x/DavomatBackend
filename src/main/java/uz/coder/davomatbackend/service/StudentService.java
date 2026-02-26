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
import uz.coder.davomatbackend.db.model.*;
import uz.coder.davomatbackend.model.Balance;
import uz.coder.davomatbackend.model.Student;
import uz.coder.davomatbackend.model.StudentCourseGroup;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static uz.coder.davomatbackend.todo.Strings.*;

@Slf4j
@Service
public class StudentService {
    private final StudentDatabase database;
    private final UserDatabase userDatabase;
    private final GroupDatabase groupDatabase;
    private final CourseDatabase courseDatabase;
    private final TelegramUserDatabase telegramUserDatabase;

    @Autowired
    public StudentService(StudentDatabase database, UserDatabase userDatabase, GroupDatabase groupDatabase, CourseDatabase courseDatabase, TelegramUserDatabase telegramUserDatabase) {
        this.database = database;
        this.userDatabase = userDatabase;
        this.groupDatabase = groupDatabase;
        this.courseDatabase = courseDatabase;
        this.telegramUserDatabase = telegramUserDatabase;
    }

    public Student save(Student student) {
        StudentDbModel save = database.save(new StudentDbModel(student.getPhoneNumber(), student.getUserId(), student.getGroupId(), LocalDate.now()));
        String firstNameById = userDatabase.findFirstNameById(student.getUserId());
        String lastNameById = userDatabase.findLastNameById(student.getUserId());
        String fullName = firstNameById + " " + lastNameById;
        return new Student(save.getId(), fullName, save.getPhoneNumber(), save.getUserId(), save.getGroupId(), save.getCreatedDate());
    }

    public Student edit(Student student) {
        database.update(student.getId(), student.getPhoneNumber(), student.getUserId(), student.getGroupId());
        StudentDbModel model = database.findById(student.getId()).orElseThrow(()->new IllegalArgumentException(THERE_IS_NO_SUCH_A_PERSON));
        String firstNameById = userDatabase.findFirstNameById(student.getUserId());
        String lastNameById = userDatabase.findLastNameById(student.getUserId());
        String fullName = firstNameById + " " + lastNameById;
        return new Student(model.getId(), fullName, model.getPhoneNumber(), model.getUserId(), model.getGroupId(), model.getCreatedDate());
    }

    public Student findById(long id) {
        StudentDbModel student = database.findById(id).orElseThrow(()->new IllegalArgumentException(THERE_IS_NO_SUCH_A_PERSON));
        String firstNameById = userDatabase.findFirstNameById(student.getUserId());
        String lastNameById = userDatabase.findLastNameById(student.getUserId());
        String fullName = firstNameById + " " + lastNameById;
        return new Student(student.getId(), fullName, student.getPhoneNumber(), student.getUserId(), student.getGroupId(), student.getCreatedDate());
    }

    public int deleteById(long id) {
        if (database.existsById(id)){
            database.deleteById(id);
            return 1;
        }else {
            return 0;
        }
    }

    public List<Student> findAllStudentByGroupId(long groupId) {
        List<StudentDbModel> allByUserId = database.findAllByGroupId(groupId);
        return getStudents(allByUserId);
    }

    public Page<Student> findAllStudentByGroupIdPaginated(long groupId, Pageable pageable) {
        Page<StudentDbModel> studentPage = database.findAllByGroupId(groupId, pageable);
        return studentPage.map(item -> {
            String firstNameById = userDatabase.findFirstNameById(item.getUserId());
            String lastNameById = userDatabase.findLastNameById(item.getUserId());
            String fullName = firstNameById + " " + lastNameById;
            return new Student(item.getId(), fullName, item.getPhoneNumber(), item.getUserId(), item.getGroupId(), item.getCreatedDate());
        });
    }
    public boolean saveAllByExcel(MultipartFile file, long userId) {
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            List<Student> studentList = new ArrayList<>();
            List<GroupDbModel> allGroups = groupDatabase.findAll();
            List<CourseDbModel> allCourses = courseDatabase.findAll();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String fullName = getCellStringValue(row.getCell(1));
                String phoneNumber = getCellStringValue(row.getCell(2));
                String groupName = getCellStringValue(row.getCell(3));
                String courseName = getCellStringValue(row.getCell(4));

                UserDbModel user = userDatabase.findByPhoneNumber(phoneNumber);
                if (user == null) {
                    String[] nameParts = fullName.trim().split(" ", 2);
                    String firstName = nameParts.length > 0 ? nameParts[0] : "";
                    String lastName = nameParts.length > 1 ? nameParts[1] : "";
                    LocalDate now = LocalDate.now();
                    LocalDate balanceDate = now.plusWeeks(1);
                    user = new UserDbModel(firstName, lastName, phoneNumber, ROLE_STUDENT, balanceDate);
                    user = userDatabase.save(user);
                }

                CourseDbModel matchedCourse = allCourses.stream()
                        .filter(c -> isSimilar(courseName, c.getTitle()))
                        .findFirst()
                        .orElseGet(() -> courseDatabase.save(new CourseDbModel(courseName, "", userId)));

                GroupDbModel matchedGroup = allGroups.stream()
                        .filter(g -> isSimilar(groupName, g.getTitle()) && g.getCourseId() == matchedCourse.getId())
                        .findFirst()
                        .orElseGet(() -> groupDatabase.save(new GroupDbModel(groupName, matchedCourse.getId())));

                Student student = new Student(fullName, phoneNumber, user.getId(), matchedGroup.getId());
                studentList.add(student);
            }

            studentList.forEach(this::accept);
            return true;

        } catch (Exception e) {
            log.error("Excel faylni o'qishda xatolik: ", e);
            return false;
        }
    }

    public byte[] exportStudentsToXlsx(List<Student> students) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Students");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue(NUMBER);
            header.createCell(1).setCellValue(FULL_NAME);
            header.createCell(2).setCellValue(PHONE);
            header.createCell(3).setCellValue(GROUP);
            header.createCell(4).setCellValue(COURSE);

            int rowNum = 1;
            for (int i = 0; i < students.size(); i++) {
                Student s = students.get(i);
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(i + 1);
                row.createCell(1).setCellValue(s.getFullName());
                row.createCell(2).setCellValue(s.getPhoneNumber());

                GroupDbModel group = groupDatabase.findById(s.getGroupId()).orElseThrow(()->new IllegalArgumentException(THERE_IS_NO_SUCH_A_PERSON));
                String groupName = group.getTitle();

                CourseDbModel course = courseDatabase.findById(group.getCourseId()).orElse(null);
                String courseName = (course != null) ? course.getTitle() : "Noma'lum";

                row.createCell(3).setCellValue(groupName);
                row.createCell(4).setCellValue(courseName);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }
    private String getCellStringValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            default -> "";
        };
    }

    private boolean isSimilar(String input, String target) {
        if (input == null || target == null) return false;
        String iNorm = normalize(input);
        String tNorm = normalize(target);
        return iNorm.contains(tNorm) || tNorm.contains(iNorm);
    }

    private String normalize(String text) {
        return text.toLowerCase()
                .replaceAll("[\\s\\-_/|]", "")
                .replace("’", "")
                .replace("'", "")
                .trim();
    }

    private void accept(Student student) {
        try {
            database.save(new StudentDbModel(student.getPhoneNumber(), student.getUserId(), student.getGroupId(), LocalDate.now()));
        } catch (Exception e) {
            log.error("e: ", e);
        }
    }

    public List<Student> getStudentsByUserId(long userId) {
        List<StudentDbModel> students = database.findAllStudentsByOwnerUserId(userId);
        return getStudents(students);
    }

    private List<Student> getStudents(List<StudentDbModel> students) {
        return students.stream().map(item -> {
            String firstNameById = userDatabase.findFirstNameById(item.getUserId());
            String lastNameById = userDatabase.findLastNameById(item.getUserId());
            String fullName = firstNameById + " " + lastNameById;
            return new Student(item.getId(), fullName, item.getPhoneNumber(), item.getUserId(), item.getGroupId(), item.getCreatedDate());
        }).collect(Collectors.toList());
    }

    public List<StudentCourseGroup> getCourseAndGroupByUserId(long userId) {
        UserDbModel model = userDatabase.findById(userId).orElseThrow(() -> new IllegalArgumentException(THERE_IS_NO_SUCH_A_PERSON));
        if (model.getRole().equals(ROLE_STUDENT)) {
            Balance balance = userDatabase.getUserBalanceById(userId);
            LocalDate now = LocalDate.now();
            if (balance.getLimit().isAfter(now)) {
                System.out.println("+");
                return findCoursesAndGroupsForStudent(userId);
            } else if (balance.getLimit().isEqual(now)) {
                System.out.println("+");
                return findCoursesAndGroupsForStudent(userId);
            } else {
                throw new IllegalArgumentException(YOUR_BALANCE_IS_EMPTY);
            }
        }else {
            throw new IllegalArgumentException(YOU_ARE_NOT_A_STUDENT);
        }
    }

    private List<StudentCourseGroup> findCoursesAndGroupsForStudent(long userId) {
        try {
            // 1. userId bo'yicha kurslarni olish
            List<CourseDbModel> courses = courseDatabase.findAllByStudentId(userId);
            if (courses.isEmpty()) {
                return List.of(); // Agar kurs yo'q bo'lsa, bo'sh list qaytariladi
            }

            // Kurs ID'larni yig'ish
            List<Long> courseIds = courses.stream()
                    .map(CourseDbModel::getId)
                    .toList();

            // 2. Kurs ID'lari bo'yicha barcha group'larni olish
            List<GroupDbModel> allGroups = groupDatabase.findGroupsByCourseIds(courseIds);

            // Group'larni kursId bo'yicha grouping qilish
            Map<Long, List<GroupDbModel>> groupsByCourse =
                    allGroups.stream()
                            .collect(Collectors.groupingBy(GroupDbModel::getCourseId));

            // 3. Har bir kurs uchun StudentCourseGroup obyektini yaratish
            return courses.stream()
                    .map(course -> new StudentCourseGroup(
                            course,
                            groupsByCourse.getOrDefault(course.getId(), List.of())
                    ))
                    .toList();

        } catch (Exception e) {
            // Xatolikni log qilish (log framework ishlatsa ham bo‘ladi)
            System.err.println("Xatolik: Student course groups olishda xato - " + e.getMessage());
            return List.of();
        }
    }


    public Balance getUserBalanceByTelegramUserId(long telegramUserId) {
        TelegramUserDbModel telegramUserDbModel = telegramUserDatabase.findByTelegramUserId(telegramUserId);
        if (telegramUserDbModel!=null){
            return userDatabase.getUserBalanceById(telegramUserDbModel.getUserId());
        }else {
            throw new IllegalArgumentException(THERE_IS_NO_SUCH_A_PERSON);
        }
    }

    public Student findByGroupIdAndUserId(long userId, long groupId) {
        boolean existsByUserId = userDatabase.existsById(userId);
        boolean existsByGroupId = groupDatabase.existsById(groupId);
        if (!existsByUserId) {
            throw new IllegalArgumentException(THERE_IS_NO_SUCH_A_PERSON);
        }else if (!existsByGroupId) {
            throw new IllegalArgumentException(THERE_IS_NO_SUCH_A_GROUP);
        }else {
            Balance balance = userDatabase.getUserBalanceById(userId);
            if (balance.getLimit().isAfter(LocalDate.now()) || balance.getLimit().isEqual(LocalDate.now())) {
                String firstName = userDatabase.findFirstNameById(userId);
                String lastName = userDatabase.findLastNameById(userId);
                String fullName = firstName+" "+lastName;
                StudentDbModel model = database.findStudentsByUserIdAndGroupId(userId, groupId);
                return new Student(model.getId(), fullName, model.getPhoneNumber(), model.getUserId(), model.getGroupId(), model.getCreatedDate());
            }else {
                throw new IllegalArgumentException(YOUR_BALANCE_IS_EMPTY);
            }
        }
    }
}