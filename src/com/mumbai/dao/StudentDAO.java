package com.mumbai.dao;

import com.mumbai.model.*;
import com.mumbai.util.DBConnection;
import java.sql.*;
import java.util.*;

public class StudentDAO {

    public static List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE is_active = TRUE ORDER BY name";
        try (Connection c = DBConnection.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static Student getById(int id) {
        String sql = "SELECT * FROM students WHERE student_id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public static Student getByRoll(String roll) {
        String sql = "SELECT * FROM students WHERE roll_number = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, roll);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public static int addStudent(Student s) {
        String sql = "INSERT INTO students (name, roll_number, email, phone, branch, current_sem, year_of_study) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getName());
            ps.setString(2, s.getRollNumber());
            ps.setString(3, s.getEmail());
            ps.setString(4, s.getPhone());
            ps.setString(5, s.getBranch());
            ps.setInt(6, s.getCurrentSem());
            ps.setInt(7, s.getYearOfStudy());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return -1;
    }

    public static boolean addSubjectMark(SubjectMark m) {
        String sql = "INSERT INTO subject_marks (student_id, semester, subject_name, subject_code, " +
                     "ia1_marks, ia2_marks, ese_marks, ese_total, practical_marks, practical_total, " +
                     "total_marks, grade_point, grade, credits, exam_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURDATE())";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, m.getStudentId());
            ps.setInt(2, m.getSemester());
            ps.setString(3, m.getSubjectName());
            ps.setString(4, m.getSubjectCode());
            ps.setDouble(5, m.getIa1Marks());
            ps.setDouble(6, m.getIa2Marks());
            ps.setDouble(7, m.getEseMarks());
            ps.setDouble(8, m.getEseTotal());
            ps.setDouble(9, m.getPracticalMarks());
            ps.setDouble(10, m.getPracticalTotal());
            ps.setDouble(11, m.getTotalMarks());
            ps.setDouble(12, m.getGradePoint());
            ps.setString(13, m.getGrade());
            ps.setInt(14, m.getCredits());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public static List<SubjectMark> getMarksBySemester(int studentId, int semester) {
        List<SubjectMark> list = new ArrayList<>();
        String sql = "SELECT * FROM subject_marks WHERE student_id = ? AND semester = ? ORDER BY subject_name";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, semester);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapMark(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static List<SubjectMark> getAllMarks(int studentId) {
        List<SubjectMark> list = new ArrayList<>();
        String sql = "SELECT * FROM subject_marks WHERE student_id = ? ORDER BY semester, subject_name";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapMark(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /** Get SGPA per semester as a list (for chart) */
    public static List<Double> getSgpaPerSemester(int studentId) {
        List<Double> sgpas = new ArrayList<>();
        String sql = "SELECT semester, " +
                     "SUM(grade_point * credits) / SUM(credits) AS sgpa " +
                     "FROM subject_marks WHERE student_id = ? " +
                     "GROUP BY semester ORDER BY semester";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) sgpas.add(rs.getDouble("sgpa"));
        } catch (SQLException e) { e.printStackTrace(); }
        return sgpas;
    }

    /** Get average percentage per semester for chart */
    public static List<Double> getPercentagePerSemester(int studentId) {
        List<Double> pcts = new ArrayList<>();
        String sql = "SELECT semester, AVG(total_marks) AS avg_pct " +
                     "FROM subject_marks WHERE student_id = ? " +
                     "GROUP BY semester ORDER BY semester";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) pcts.add(rs.getDouble("avg_pct"));
        } catch (SQLException e) { e.printStackTrace(); }
        return pcts;
    }

    public static double getAvgAttendance(int studentId) {
        String sql = "SELECT AVG(percentage) FROM attendance WHERE student_id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 75;
    }

    public static boolean addAttendance(int studentId, int sem, String subject, int total, int attended) {
        double pct = total > 0 ? (attended * 100.0 / total) : 0;
        String sql = "INSERT INTO attendance (student_id, semester, subject_name, total_lectures, attended, percentage) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, studentId); ps.setInt(2, sem); ps.setString(3, subject);
            ps.setInt(4, total); ps.setInt(5, attended); ps.setDouble(6, pct);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public static void updateCgpa(int studentId) {
        String sql = "UPDATE students SET cgpa = (" +
                     "SELECT SUM(grade_point * credits) / SUM(credits) FROM subject_marks WHERE student_id = ?" +
                     "), total_percentage = (" +
                     "SELECT AVG(total_marks) FROM subject_marks WHERE student_id = ?" +
                     ") WHERE student_id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, studentId); ps.setInt(2, studentId); ps.setInt(3, studentId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private static Student map(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setStudentId(rs.getInt("student_id"));
        s.setName(rs.getString("name"));
        s.setRollNumber(rs.getString("roll_number"));
        s.setEmail(rs.getString("email"));
        s.setPhone(rs.getString("phone"));
        s.setBranch(rs.getString("branch"));
        s.setCurrentSem(rs.getInt("current_sem"));
        s.setYearOfStudy(rs.getInt("year_of_study"));
        s.setCgpa(rs.getDouble("cgpa"));
        s.setTotalPercentage(rs.getDouble("total_percentage"));
        return s;
    }

    private static SubjectMark mapMark(ResultSet rs) throws SQLException {
        SubjectMark m = new SubjectMark();
        m.setMarkId(rs.getInt("mark_id"));
        m.setStudentId(rs.getInt("student_id"));
        m.setSemester(rs.getInt("semester"));
        m.setSubjectName(rs.getString("subject_name"));
        m.setSubjectCode(rs.getString("subject_code"));
        m.setIa1Marks(rs.getDouble("ia1_marks"));
        m.setIa2Marks(rs.getDouble("ia2_marks"));
        m.setEseMarks(rs.getDouble("ese_marks"));
        m.setEseTotal(rs.getDouble("ese_total"));
        m.setPracticalMarks(rs.getDouble("practical_marks"));
        m.setPracticalTotal(rs.getDouble("practical_total"));
        m.setTotalMarks(rs.getDouble("total_marks"));
        m.setGradePoint(rs.getDouble("grade_point"));
        m.setGrade(rs.getString("grade"));
        m.setCredits(rs.getInt("credits"));
        return m;
    }
}
