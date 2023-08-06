package com.htuozhou.wvp.common.easyexcel;

import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;

import java.util.Map;

/**
 * @author hanzai
 * @date 2023/2/3
 */
public class ImportWriteHandler implements SheetWriteHandler {

    // 下拉框赋值
    Map<Integer, String[]> mapDropDown;

    // 合并的列数
    private int rangeRow;

    // 说明
    private String description;

    public ImportWriteHandler(String description, Map<Integer, String[]> mapDropDown, int rangeRow) {
        this.mapDropDown = mapDropDown;
        this.rangeRow = rangeRow;
        this.description = description;
    }


    @Override
    public void beforeSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {

    }

    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        Workbook workbook = writeWorkbookHolder.getWorkbook();
        Sheet sheet = workbook.getSheetAt(0);
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue(description);
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setBorderRight(BorderStyle.THIN);
        Font font = workbook.createFont();
        font.setColor((short) 10);
        font.setBold(true);
        cellStyle.setFont(font);
        cell.setCellStyle(cellStyle);
        Cell cellRange = row.createCell(rangeRow);
        cellRange.setCellStyle(cellStyle);

        // 合并
        sheet.addMergedRegionUnsafe(new CellRangeAddress(0, 0, 0, rangeRow));

        // 设置下拉框
        DataValidationHelper helper = sheet.getDataValidationHelper();
        for (Map.Entry<Integer, String[]> entry : mapDropDown.entrySet()) {
            CellRangeAddressList addressList = new CellRangeAddressList(2, 1000000, entry.getKey(), entry.getKey());
            DataValidationConstraint constraint = helper.createExplicitListConstraint(entry.getValue());
            DataValidation dataValidation = helper.createValidation(constraint, addressList);
            if (dataValidation instanceof XSSFDataValidation) {
                dataValidation.setSuppressDropDownArrow(true);
                dataValidation.setShowErrorBox(true);
            } else {
                dataValidation.setSuppressDropDownArrow(false);
            }
            sheet.addValidationData(dataValidation);
        }

    }

}
