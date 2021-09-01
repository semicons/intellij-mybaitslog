package com.plugins.mybaitslog.action;

import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.project.Project;
import com.plugins.mybaitslog.icons.Icons;
import com.plugins.mybaitslog.util.*;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;


/**
 * 控制台 右键 启动Sql格式化输出窗口
 *
 * @author lk
 * @version 1.0
 * @date 2020/8/23 17:14
 */
public class RestoreSqlForSelection extends AnAction {


    public RestoreSqlForSelection() {
        super(Icons.MyBatisIcon);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        final Project project = e.getProject();
        if (project == null) {
            return;
        }
        CaretModel caretModel = Objects.requireNonNull(e.getData(LangDataKeys.EDITOR)).getCaretModel();
        Caret currentCaret = caretModel.getCurrentCaret();
        String selectedText = currentCaret.getSelectedText();
        ConfigUtil.setShowMyBatisLog(project);
        final String preparing = ConfigUtil.getPreparing();
        final String parameters = ConfigUtil.getParameters();
        if (StringUtils.isNotEmpty(selectedText)) {
            //分割每一行
            String[] selectedRowText = selectedText.split("\n");
            if (isKeyWord(project, selectedText, selectedRowText, preparing, parameters)) {
                setFormatSelectedText(project, selectedRowText, preparing, parameters);
            }
        }
    }


    @Override
    public void update(@NotNull AnActionEvent event) {
        this.getTemplatePresentation().setEnabled(true);
    }

    /**
     * 是否存在关键字文本
     *
     * @param project         项目
     * @param selectedText    文本
     * @param selectedRowText 文本
     * @param preparing       关键字
     * @param parameters      关键字
     */
    private boolean isKeyWord(Project project, String selectedText, String[] selectedRowText, String preparing, String parameters) {
        if (StringUtils.isNotBlank(selectedText) && selectedText.contains(preparing) && selectedText.contains(parameters)) {
            //必须大于两行,MyBatis输出有两行关键信息
            if (selectedRowText.length >= 2) {
                return true;
            }
        }
        PrintlnUtil.println(project, "", ConsoleViewContentType.USER_INPUT);
        PrintlnUtil.println(project, KeyNameUtil.SQL_NULL, ConsoleViewContentType.USER_INPUT,true);
        return false;
    }

    /**
     * 设置显示的文本,局部
     *
     * @param project         项目
     * @param selectedRowText 文本
     * @param preparing       关键字
     * @param parameters      关键字
     */
    private void setFormatSelectedText(Project project, String[] selectedRowText, String preparing, String parameters) {
        String preparingLine = "";
        String parametersLine = "";
        for (int i = 0; i < selectedRowText.length; ++i) {
            String currentLine = selectedRowText[i];
            //第一个关键字
            if (currentLine.contains(preparing)) {
                preparingLine = currentLine;
                continue;
            }
            //第一行不为空的情况下,找寻第二个关键字
            if (!StringUtils.isEmpty(preparingLine) && currentLine.contains(parameters)) {
                parametersLine = currentLine;
            } else {
                continue;
            }
            if (StringUtils.isNotEmpty(preparingLine) && StringUtils.isNotEmpty(parametersLine)) {
                //SQL还原
                String[] restoreSql = SqlProUtil.restoreSql(preparingLine, parametersLine);
                PrintlnUtil.println(project, KeyNameUtil.SQL_Line + restoreSql[0], ConsoleViewContentType.USER_INPUT);
                //高亮显示
                PrintlnUtil.printlnSqlType(project, restoreSql[1]);
            } else {
                PrintlnUtil.println(project, "", ConsoleViewContentType.USER_INPUT);
                PrintlnUtil.println(project, KeyNameUtil.SQL_NULL, ConsoleViewContentType.USER_INPUT,true);
            }
        }
    }

}