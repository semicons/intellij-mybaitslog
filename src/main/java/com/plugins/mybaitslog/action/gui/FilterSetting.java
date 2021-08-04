package com.plugins.mybaitslog.action.gui;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;
import com.plugins.mybaitslog.util.ConfigUtil;
import com.plugins.mybaitslog.util.KeyNameUtil;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * 过滤设置 窗口
 *
 * @author lk
 * @version 1.0
 * @date 2020/8/23 17:14
 */
public class FilterSetting extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField preparingTextField;
    private JTextField parametersTextField;
    private JTextField totalTextField;
    private JCheckBox startupCheckBox;

    /**
     * 窗口初始化
     *
     * @param project 项目
     */
    public FilterSetting(Project project) {
        //设置标题
        this.setTitle("Filter Setting");
        this.preparingTextField.setText(ConfigUtil.getPreparing());
        this.parametersTextField.setText(ConfigUtil.getParameters());
        this.totalTextField.setText(ConfigUtil.getTotal());
        int startup = PropertiesComponent.getInstance(project).getInt(KeyNameUtil.DB_STARTUP_KEY, 1);
        startupCheckBox.setSelected(startup == 1);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        buttonOK.addActionListener(e -> onOK(project));
        buttonCancel.addActionListener(e -> onCancel());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    /**
     * 点击确认按钮处理
     *
     * @param project 项目
     */
    private void onOK(Project project) {
        String preparing = this.preparingTextField.getText();
        String parameters = this.parametersTextField.getText();
        String total = this.totalTextField.getText();
        ConfigUtil.setPreparing(preparing, KeyNameUtil.PREPARING);
        ConfigUtil.setParameters(parameters, KeyNameUtil.PARAMETERS);
        ConfigUtil.setTotal(total, KeyNameUtil.TOTAL);
        ConfigUtil.setStartup(startupCheckBox.isSelected() ? 1 : 0);
        this.setVisible(false);
    }

    private void onCancel() {
        this.setVisible(false);
    }
}
