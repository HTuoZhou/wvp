package com.htuozhou.wvp.common.easyexcel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author hanzai
 * @date 2023/2/7
 */
@Slf4j
public class ImportListener<T> extends AnalysisEventListener<T> {

    private final ImportDataAssistService<T> assistService;
    private final StringBuilder errorMsg;
    private final List<T> bos;
    private Boolean head;

    public ImportListener(ImportDataAssistService<T> assistService) {
        this.assistService = assistService;
        this.head = Boolean.TRUE;
        this.errorMsg = new StringBuilder();
        this.bos = new ArrayList<>();
    }

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        if (context.readRowHolder().getRowIndex() == 1) {
            List<String> templateHeads = assistService.getTemplateHeadName();
            for (int i = 0; i < templateHeads.size(); i++) {
                if (!Objects.equals(templateHeads.get(i), headMap.get(i))) {
                    head = Boolean.FALSE;
                }
            }
        }
    }

    @Override
    public void invoke(T data, AnalysisContext context) {
        assistService.verifyImportData(data, context.readRowHolder().getRowIndex() + 1, errorMsg);
        bos.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }

    public Boolean getHead() {
        return head;
    }

    public StringBuilder getErrorMsg() {
        return errorMsg;
    }

    public List<T> getBos() {
        return bos;
    }
}