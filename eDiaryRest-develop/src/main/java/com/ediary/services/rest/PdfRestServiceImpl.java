package com.ediary.services.rest;

import com.ediary.domain.Grade;
import com.ediary.domain.Student;
import com.ediary.domain.Subject;
import com.ediary.domain.Teacher;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class PdfRestServiceImpl implements PdfRestService {

    @Override
    public Boolean createStudentCardPdf(HttpServletResponse response,
                                        Map<String, List<Grade>> gradesWithSubjects, Student student,
                                        Map<String, Long> attendanceNumber, String timeInterval, String behaviorGrade) throws Exception {
        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf);


        PdfFont normalTextFont;

        try {
            FontProgram fontProgram = FontProgramFactory.createFont();
            normalTextFont = PdfFontFactory.createFont(fontProgram, "Cp1257");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        //Date
        Table table = getNewTable(new float[]{100});
        table.addCell(getCell(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")), TextAlignment.RIGHT,
                10f, normalTextFont));
        doc.add(table);


        //Separator
        doc.add(new LineSeparator(new SolidLine(3)));


        //Main title
        String studentNameAndClass = student.getUser().getFirstName() + " " + student.getUser().getLastName() + "  " +
                student.getSchoolClass().getName();
        table = getNewTable(new float[]{100});

        table.addCell(getCell("\n" + "Wykaz ocen: " + studentNameAndClass,
                TextAlignment.CENTER, 22f, normalTextFont));

        table.addCell(getCell("Przedział: " + timeInterval + "\n\n\n\n", TextAlignment.CENTER, 13f, normalTextFont));

        doc.add(table);

        //Grades
        table = getNewTable(new float[]{40, 60});
        table.addCell(getHeaderCell("Przedmiot", TextAlignment.CENTER, 12f, normalTextFont));
        table.addCell(getHeaderCell("Oceny", TextAlignment.CENTER, 12f, normalTextFont));
        Paragraph paragraph;

        for (Map.Entry<String, List<Grade>> entry : gradesWithSubjects.entrySet()) {
            StringBuilder gradesPerSubject = new StringBuilder();

            //subject name
            paragraph = new Paragraph(entry.getKey());
            paragraph.setFont(normalTextFont);
            table.addCell(paragraph);

            //all grades for subject
            paragraph = new Paragraph();
            for (Grade grade : entry.getValue()) {
                if (grade != null) {
                    gradesPerSubject.append(grade.getValue()).append(", ");
                }
            }

            if (gradesPerSubject.length() != 0) {
                gradesPerSubject.delete(gradesPerSubject.length() - 2, gradesPerSubject.length() - 1);
            }

            paragraph.add(gradesPerSubject.toString());
            table.addCell(paragraph);
        }

        table.addCell(getCell("\n\n", TextAlignment.CENTER, 12f, normalTextFont));
        table.addCell(getCell("", TextAlignment.CENTER, 12f, normalTextFont));
        doc.add(table);


        //Behavior
        table = getNewTable(new float[]{40, 60});
        table.addCell(getCell("Zachowanie: ", TextAlignment.CENTER, 12f, normalTextFont));
        table.addCell(getCell(behaviorGrade, TextAlignment.CENTER, 12f, normalTextFont));
        doc.add(table);

        //Attendances
        table = getNewTable(new float[]{40, 60});
        table.addCell(getCell("Nieobecności: ", TextAlignment.CENTER, 12f, normalTextFont));
        table.addCell(getCell("- ogółem: " + attendanceNumber.get("total"), TextAlignment.CENTER, 12f, normalTextFont));
        table.addCell(getCell("", TextAlignment.CENTER, 12f, normalTextFont));
        table.addCell(getCell("- w tym usprawiedliwione: " + attendanceNumber.get("excused"),
                TextAlignment.CENTER, 12f, normalTextFont));


        doc.add(table);

        doc.close();

        return true;
    }

    @Override
    public byte[] createReportPdf(Teacher teacher, String timeInterval,
                                   Integer lessonsNumber, String subjectsNames, Long gradesNumber,
                                   Long eventsNumber) throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf);

        PdfFont normalTextFont;

        try {
            FontProgram fontProgram = FontProgramFactory.createFont();
            normalTextFont = PdfFontFactory.createFont(fontProgram, "Cp1257");
        } catch (IOException e) {
            e.printStackTrace();
            return baos.toByteArray();
        }

        //Date
        Table table = getNewTable(new float[]{100});
        table.addCell(getCell(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")), TextAlignment.RIGHT,
                10f, normalTextFont));
        doc.add(table);


        //Separator
        doc.add(new LineSeparator(new SolidLine(3)));


        //Main title
        table = getNewTable(new float[]{100});
        String teacherName = teacher.getUser().getFirstName() + " " + teacher.getUser().getLastName();


        table.addCell(getCell("\n" + "Raport: " + teacherName,
                TextAlignment.CENTER, 22f, normalTextFont));
        table.addCell(getCell("Przedział: " + timeInterval + "\n\n\n", TextAlignment.CENTER, 13f, normalTextFont));

        doc.add(table);



        //New Table for teacher info
        table = getNewTable(new float[]{45, 55});

        String address = "";
        if (teacher.getUser().getAddress() != null) {
            address = teacher.getUser().getAddress().getStreet() + "\n" +
                    teacher.getUser().getAddress().getZip() + " " + teacher.getUser().getAddress().getCity() + "\n";
        }


        //Teacher Address
        fillNewRow(table, normalTextFont, "Adres: ", address);

        //Number of subjects
        fillNewRow(table, normalTextFont, "Przedmioty: ", subjectsNames);

        //Number of teacher's lesson
        fillNewRow(table, normalTextFont, "Liczba przeprowadzonych zajęć: ", lessonsNumber.toString());

        //Number of grades
        fillNewRow(table, normalTextFont, "Liczba wystawionych ocen: ", gradesNumber + "");

        //Made events
        fillNewRow(table, normalTextFont, "Stworzone wydarzenia: ", eventsNumber + "");

        //Is teacher form tutor
        fillNewRow(table, normalTextFont, "Wychowawca: ", (teacher.getSchoolClass() != null ? "tak" : "nie"));

        //If form tutor show additional info
        if (teacher.getSchoolClass() != null) {
            fillNewRow(table, normalTextFont, "Klasa: ", teacher.getSchoolClass().getName());
            fillNewRow(table, normalTextFont, "Liczba studentów: ", teacher.getSchoolClass().getStudents().size() + "");
        }


        doc.add(table);

        doc.close();

        return baos.toByteArray();
    }


    @Override
    public byte[] createEndYearReportStudent(Map<Subject, List<Grade>> gradesWithSubjects,
                                             Map<Long, Grade> finalGrades,
                                             Student student,
                                             Map<String, Long> attendanceNumber, String behaviorGrade, Integer year) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf);


        PdfFont normalTextFont;

        try {
            FontProgram fontProgram = FontProgramFactory.createFont();
            normalTextFont = PdfFontFactory.createFont(fontProgram, "Cp1257");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        //Date
        Table table = getNewTable(new float[]{100});
        table.addCell(getCell(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")), TextAlignment.RIGHT,
                10f, normalTextFont));
        doc.add(table);


        //Separator
        doc.add(new LineSeparator(new SolidLine(3)));


        //Main title
        String studentNameAndClass;
        if (student.getSchoolClass() == null) {
            studentNameAndClass = student.getUser().getFirstName() + " " + student.getUser().getLastName();
        } else {
            studentNameAndClass = student.getUser().getFirstName() + " " + student.getUser().getLastName() + "  " +
                    student.getSchoolClass().getName();
        }


        table = getNewTable(new float[]{100});

        table.addCell(getCell("\n" + "Karta końcowo-roczna: " + studentNameAndClass,
                TextAlignment.CENTER, 22f, normalTextFont));
        table.addCell(getCell( "Rok: "  + year + "\n\n",
                TextAlignment.CENTER, 22f, normalTextFont));

        doc.add(table);

        //Grades
        table = getNewTable(new float[]{30, 50, 20});
        table.addCell(getHeaderCell("Przedmiot", TextAlignment.CENTER, 12f, normalTextFont));
        table.addCell(getHeaderCell("Oceny", TextAlignment.CENTER, 12f, normalTextFont));
        table.addCell(getHeaderCell("Ocena końcowa", TextAlignment.CENTER, 12f, normalTextFont));
        Paragraph paragraph;

        if (gradesWithSubjects != null) {
            for (Map.Entry<Subject, List<Grade>> entry : gradesWithSubjects.entrySet()) {
                StringBuilder gradesPerSubject = new StringBuilder();

                //subject name
                paragraph = new Paragraph(entry.getKey().getName());
                paragraph.setFont(normalTextFont);
                table.addCell(paragraph);

                //all grades for subject
                paragraph = new Paragraph();
                for (Grade grade : entry.getValue()) {
                    if (grade != null) {
                        gradesPerSubject.append(grade.getValue()).append(", ");
                    }
                }

                if (gradesPerSubject.length() != 0) {
                    gradesPerSubject.delete(gradesPerSubject.length() - 2, gradesPerSubject.length() - 1);
                }

                paragraph.add(gradesPerSubject.toString());
                table.addCell(paragraph);


                //final grade
                paragraph = new Paragraph();
                if (finalGrades.get(entry.getKey().getId()) != null) {
                    paragraph.add(finalGrades.get(entry.getKey().getId()).toString());
                } else paragraph.add("");

                table.addCell(paragraph);
            }
        } else {
            paragraph = new Paragraph("nie znaleziono");
            paragraph.setFont(normalTextFont);

            for (int i = 0; i < 3; i++) {
                table.addCell(paragraph);
            }
        }

        table.addCell(getCell("\n\n", TextAlignment.CENTER, 12f, normalTextFont));
        table.addCell(getCell("", TextAlignment.CENTER, 12f, normalTextFont));
        table.addCell(getCell("", TextAlignment.CENTER, 12f, normalTextFont));
        doc.add(table);


        //Behavior
        table = getNewTable(new float[]{40, 60});
        table.addCell(getCell("Zachowanie: ", TextAlignment.CENTER, 12f, normalTextFont));
        table.addCell(getCell(behaviorGrade, TextAlignment.CENTER, 12f, normalTextFont));
        doc.add(table);

        //Attendances
        table = getNewTable(new float[]{40, 60});
        table.addCell(getCell("Nieobecności: ", TextAlignment.CENTER, 12f, normalTextFont));
        table.addCell(getCell("- ogółem: " + attendanceNumber.get("total"), TextAlignment.CENTER, 12f, normalTextFont));
        table.addCell(getCell("", TextAlignment.CENTER, 12f, normalTextFont));
        table.addCell(getCell("- w tym usprawiedliwione: " + attendanceNumber.get("excused"),
                TextAlignment.CENTER, 12f, normalTextFont));


        doc.add(table);

        doc.close();

        return baos.toByteArray();
    }


    @Override
    public byte[] createEndYearReportTeacher(Map<Subject, Map<Student, List<Grade>>> listSubjectsStudentsWithGrades,
                                             Map<Long, Map<Student, Grade>> finalGrades, Teacher teacher, Integer year) {



        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf);


        PdfFont normalTextFont;

        try {
            FontProgram fontProgram = FontProgramFactory.createFont();
            normalTextFont = PdfFontFactory.createFont(fontProgram, "Cp1257");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        //Date
        Table table = getNewTable(new float[]{100});
        table.addCell(getCell(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")), TextAlignment.RIGHT,
                10f, normalTextFont));
        doc.add(table);


        //Separator
        doc.add(new LineSeparator(new SolidLine(3)));


        //Main title
        String teacherName = teacher.getUser().getFirstName() + " " + teacher.getUser().getLastName();
        table = getNewTable(new float[]{100});

        table.addCell(getCell("\n" + "Karta końcowo-roczna: "  + teacherName ,
                TextAlignment.CENTER, 22f, normalTextFont));
        table.addCell(getCell("Rok: "  + year ,
                TextAlignment.CENTER, 22f, normalTextFont));

        doc.add(table);

        int currentPageIndex = 0;
        for (Map.Entry<Subject, Map<Student, List<Grade>>> entry : listSubjectsStudentsWithGrades.entrySet()) {


            //subject name
            table = getNewTable(new float[]{100});

            table.addCell(getCell("\n" + entry.getKey().getName() + "\n\n",
                    TextAlignment.CENTER, 22f, normalTextFont));

            doc.add(table);

            //Grades
            table = getNewTable(new float[]{30, 50, 20});
            table.addCell(getHeaderCell("Uczeń", TextAlignment.CENTER, 12f, normalTextFont));
            table.addCell(getHeaderCell("Oceny", TextAlignment.CENTER, 12f, normalTextFont));
            table.addCell(getHeaderCell("Ocena końcowa", TextAlignment.CENTER, 12f, normalTextFont));
            Paragraph paragraph;


            for (Map.Entry<Student, List<Grade>> studentListEntry : entry.getValue().entrySet()) {
                StringBuilder gradesPerSubject = new StringBuilder();

                //student name
                paragraph = new Paragraph(studentListEntry.getKey().getUser().getFirstName() +
                        " " + studentListEntry.getKey().getUser().getLastName());
                paragraph.setFont(normalTextFont);
                table.addCell(paragraph);

                //all grades for subject
                paragraph = new Paragraph();
                for (Grade grade : studentListEntry.getValue()) {
                    if (grade != null) {
                        gradesPerSubject.append(grade.getValue()).append(", ");
                    }
                }

                if (gradesPerSubject.length() > 1) {
                    gradesPerSubject.delete(gradesPerSubject.length() - 2, gradesPerSubject.length() - 1);
                }

                paragraph.add(gradesPerSubject.toString());
                table.addCell(paragraph);


                //final grade
                paragraph = new Paragraph();
                if (finalGrades.get(entry.getKey().getId()) != null &&
                        finalGrades.get(entry.getKey().getId()).get(studentListEntry.getKey()) != null) {
                    paragraph.add(finalGrades.get(entry.getKey().getId()).get(studentListEntry.getKey()).getValue());
                } else paragraph.add("");

                table.addCell(paragraph);
            }


            //add table
            doc.add(table);
            currentPageIndex++;

            //if subject isnt last one, add new page
            if (listSubjectsStudentsWithGrades.size() != currentPageIndex) {
                doc.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
            }

        }


        doc.close();

        return baos.toByteArray();
    }

    private Cell getCell(String text, TextAlignment alignment, Float fontSize, PdfFont normalTextFont) {
        Cell cell = new Cell().add(new Paragraph(text));
        cell.setPadding(0);
        cell.setFontSize(fontSize);
        cell.setTextAlignment(alignment);
        cell.setBorder(Border.NO_BORDER);
        cell.setFont(normalTextFont);
        cell.setHorizontalAlignment(HorizontalAlignment.CENTER);
        cell.setVerticalAlignment(VerticalAlignment.MIDDLE);
        return cell;
    }


    private Cell getHeaderCell(String text, TextAlignment alignment, Float fontSize, PdfFont normalTextFont) {
        Cell cell = new Cell().add(new Paragraph(text));
        cell.setPadding(0);
        cell.setFontSize(fontSize);
        cell.setBold();
        cell.setTextAlignment(alignment);
        cell.setFont(normalTextFont);
        cell.setHorizontalAlignment(HorizontalAlignment.CENTER);
        cell.setVerticalAlignment(VerticalAlignment.MIDDLE);
        return cell;
    }

    private Table getNewTable(float[] columnWidth) {
        return new Table(UnitValue.createPercentArray(columnWidth)).useAllAvailableWidth();
    }


    private void fillNewRow(Table table, PdfFont normalTextFont, String... columnContent) {
        for (String content : columnContent) {
            Paragraph paragraph = new Paragraph(content);
            paragraph.setFont(normalTextFont);
            table.addCell(paragraph);
        }
    }


}
