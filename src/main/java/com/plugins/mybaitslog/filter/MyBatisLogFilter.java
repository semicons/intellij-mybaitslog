package com.plugins.mybaitslog.filter;

import com.intellij.execution.filters.Filter;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.Project;
import com.plugins.mybaitslog.util.*;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * 语句过滤器
 *
 * @author lk
 * @version 1.0
 * @date 2020/4/10 22:00
 */
public class MyBatisLogFilter implements Filter {

    private final Project project;
    private static String preparingLine = "";
    private static String parametersLine = "";

    public MyBatisLogFilter(Project project) {
        this.project = project;
    }

    @Nullable
    @Override
    public Result applyFilter(final String currentLine, int endPoint) {
        if (this.project == null) {
            return null;
        }
        final boolean startup = ConfigUtil.getStartup();
        if (startup && currentLine != null) {
            prints(currentLine, endPoint);
        }
        return null;
    }

    private void prints(final String currentLine, int endPoint) {
        final String preparing = ConfigUtil.getPreparing();
        final String parameters = ConfigUtil.getParameters();
        if (currentLine.contains(preparing)) {
            preparingLine = currentLine;
        }
        if (!StringUtils.isEmpty(preparingLine) && currentLine.contains(parameters)) {
            parametersLine = currentLine;
        }
        if (StringUtils.isNotEmpty(preparingLine) && StringUtils.isNotEmpty(parametersLine)) {
            //序号前缀字符串
            String[] restoreSql = SqlProUtil.restoreSql(preparingLine, parametersLine);
            PrintlnUtil.println(project, KeyNameUtil.SQL_Line + restoreSql[0], ConsoleViewContentType.USER_INPUT);
            PrintlnUtil.printlnSqlType(project, restoreSql[1]);
//            PrintlnUtil.println(project, "", ConsoleViewContentType.USER_INPUT);
//            PrintlnUtil.println(project, KeyNameUtil.SQL_Line.concat(restoreSql[2]), ConsoleViewContentType.USER_INPUT);
            StringBuilder splitLine = new StringBuilder();
            for (int i = 0; i < (KeyNameUtil.SQL_Line.concat(restoreSql[0]).length()); i++) {
                splitLine.append("=");
            }
            PrintlnUtil.println(project,splitLine.toString() ,
                    new ConsoleViewContentType("styleName", new TextAttributes(new Color(200,215,0), null, null, null, Font.PLAIN)));
            PrintlnUtil.println(project, "", ConsoleViewContentType.USER_INPUT);
            preparingLine = "";
            parametersLine = "";
        }
    }
}
