import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.OutputStream;
import java.util.List;

public class ExcelWriter {

    public <T> void export(int flushLimitRowCount, OutputStream outputStream, List<T> data) {
        SXSSFWorkbook workbook = new SXSSFWorkbook(flushLimitRowCount);
        Sheet sheet = workbook.createSheet("naverHotel");
    }


}
