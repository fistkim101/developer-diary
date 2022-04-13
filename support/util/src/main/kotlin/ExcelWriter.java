import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ExcelWriter {

    public <T> void export(int flushLimitRowCount, OutputStream outputStream, List<T> data) throws IOException {
        if (data.isEmpty()) {
            return; // TODO 에러처리
        }

        SXSSFWorkbook workbook = new SXSSFWorkbook(flushLimitRowCount);
        Sheet sheet = workbook.createSheet("sheetName");

        this.fillHeader(workbook, sheet, data);
        this.fillContents(sheet, data);

        workbook.write(outputStream);
        workbook.dispose();
    }

    private <T> void fillContents(Sheet sheet, List<T> data) {
        int CONTENTS_START_ROW_NUMBER = 1;

        Class<?> targetClass = data.get(0).getClass();
        AtomicInteger rowCount = new AtomicInteger(CONTENTS_START_ROW_NUMBER);
        data.forEach(element -> {
            Row row = sheet.createRow(rowCount.getAndIncrement());
            this.fillContentsRow(row, element);
        });

    }

    private <T> void fillContentsRow(Row row, T element) {
        AtomicInteger cellCount = new AtomicInteger();
        Arrays.stream(element.getClass().getDeclaredFields())
                .forEach(field -> {
                    Cell cell = row.createCell(cellCount.getAndIncrement());
                    try {
                        cell.setCellValue(field.get(element).toString());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace(); // TODO 에러처리
                    }
                });
    }

    private <T> void fillHeader(Workbook workbook, Sheet sheet, List<T> data) {
        int HEADER_ROW_NUMBER = 0;

        CellStyle headerStyle = this.getHeaderStyle(workbook);
        Row row = sheet.createRow(HEADER_ROW_NUMBER);

        AtomicInteger cellCount = new AtomicInteger();
        Arrays.stream(data.get(0).getClass().getDeclaredFields())
                .forEach(element -> {
                    Cell cell = row.createCell(cellCount.getAndIncrement());
                    cell.setCellStyle(headerStyle);
                    cell.setCellValue(element.getName());
                });
    }

    private CellStyle getHeaderStyle(Workbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();

        // border
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);

        // background color
        headerStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.SKY_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        return headerStyle;
    }

}
