package com.finance;
import com.finance.tools.excel_tools;
import com.finance.finace_message_class.basic_info;
import com.finance.finace_message_class.gggd;
import com.finance.finace_message_class.sequential_financial;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class get_excelmessage {
    public static basic_info get_basic_information(String path,int fileindex){
        ArrayList<String> files=excel_tools.getFiles(path);//获取目录下所有文件名
        XSSFWorkbook workbook=excel_tools.getWorkbook(files,fileindex);
        String companyname=excel_tools.getcompany_name(files,fileindex);
        Map<String,String> basic_information=excel_tools.get_basicinfo(workbook);
        List<List> customer=excel_tools.get_boxinfo(workbook,"主要客户情况表");
        List<List> supplier=excel_tools.get_boxinfo(workbook,"主要供应商情况表");
        basic_info basicmessage=new basic_info();
        basicmessage.companyname=companyname;
        basicmessage.basic_information=basic_information;
        basicmessage.customer=customer;
        basicmessage.supplier=supplier;
        return basicmessage;
    }
    public static gggd get_gggd(String path,int fileindex){
        ArrayList<String> files=excel_tools.getFiles(path);
        XSSFWorkbook workbook=excel_tools.getWorkbook(files,fileindex);
        String companyname=excel_tools.getcompany_name(files,fileindex);
        List<List> gudong=excel_tools.get_gudongbox(workbook,"股东");
        List<List> gaoguan=excel_tools.get_boxinfo(workbook,"高管");
        gggd allmessage=new gggd();
        allmessage.companyname=companyname;
        allmessage.gudong=gudong;
        allmessage.gaoguan=gaoguan;
        return allmessage;
    }
    public static sequential_financial get_financial(String path,int fileindex){
        ArrayList<String> files = excel_tools.getFiles(path);
        XSSFWorkbook workbook=excel_tools.getWorkbook(files,fileindex);
        sequential_financial financial_message=new sequential_financial();
        financial_message.companyname=excel_tools.getcompany_name(files,fileindex);
        financial_message.profitability=excel_tools.get_boxinfo(workbook,"盈利能力表");
        financial_message.solvency= excel_tools.get_boxinfo(workbook,"偿债能力表");
        financial_message.operation=excel_tools.get_boxinfo(workbook,"营运情况表");
        financial_message.growth=excel_tools.get_boxinfo(workbook,"成长情况");;
        financial_message.capital_stock=excel_tools.get_boxinfo(workbook,"普通股总股本表");;
        financial_message.non_recurrent_lossprofit=excel_tools.get_boxinfo(workbook,"非经常性损益表");;
        return financial_message;
    }
    public static void main(String[] args){
        gggd gggdmessage=get_gggd("C:\\Users\\renyi\\IdeaProjects\\finance_knowledgegraph\\src\\data\\2016wordextra",0);
        System.out.println(gggdmessage.gudong);
    }
}
