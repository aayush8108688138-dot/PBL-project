package com.analytics.student.db;

import com.analytics.student.model.Student;
import java.util.List;
import java.util.Optional;

public interface StudentDao {
    void insert(Student s);
    List<Student> findAll();
    Optional<Student> findById(String id);
    void update(Student s);
    void delete(String id);
    List<Student> findByPerformanceGroup(String group);
}
