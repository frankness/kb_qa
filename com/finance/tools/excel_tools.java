package com.finance.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class excel_tools{
    public static ArrayList<String> getFiles(String path) {
        ArrayList<String> files = new ArrayList<String>();
        File file = new File(path);
        File[] tempList = file.listFiles();
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                files.add(tempList[i].toString());
            }
            if (tempList[i].isDirectory()) {
            }
        }
        return files;
    }
    public static XSSFWorkbook getWorkbook(ArrayList<String> files,int fileIndex) {
        FileInputStream excelFileInputStream = null;
        //        for (int fileIndex = 0; fileIndex < files.size(); fileIndex++) {
        try {
            excelFileInputStream = new FileInputStream(files.get(fileIndex));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook(excelFileInputStream);
            excelFileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
//            }
        }
        return workbook;
    }
    public static String getcompany_name(ArrayList<String> files,int fileIndex){
        Pattern pattern = Pattern.compile("\\d+wordextra\\\\\\d+(.*)：");
        String find=new String();
        String findflie=files.get(fileIndex);
        Matcher matcher = pattern.matcher(findflie);
        while (matcher.find()) {
            find=matcher.group(1);
            //System.out.println(find);
        }
        //System.out.println(find);
        return find;
    }

    public static Map<String,String> get_basicinfo(XSSFWorkbook workbook){
        Map <String,String> basic_information=new HashMap<String,String>();
        XSSFSheet sheet = workbook.getSheet("公司基本信息表");
        try{
            for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                XSSFRow row = sheet.getRow(rowIndex);
                if (row == null) {
                    continue;
                }
                //int columnNum=row.getPhysicalNumberOfCells();
                basic_information.put(row.getCell(0).getStringCellValue(), row.getCell(1).getStringCellValue());
            }
        }catch (Exception e){
           // System.out.println(e);
        }
        return basic_information;
    }
    public static List<List>  get_boxinfo(XSSFWorkbook workbook, String sheetname){
        List<List> boxinfo=new ArrayList<List>();
        XSSFSheet sheet = workbook.getSheet(sheetname);
        try {
            int columnnum = sheet.getRow(0).getLastCellNum();
            for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                List<String> rowmessage = new ArrayList<String>();
                XSSFRow row = sheet.getRow(rowIndex);
                if (row == null) {
                    continue;
                }
                //int columnNum = row.getPhysicalNumberOfCells();
                for (int columnIndex = 0; columnIndex < columnnum; columnIndex++) {
                    try {
                        String got_message = row.getCell(columnIndex) == null ? "-" : row.getCell(columnIndex).getStringCellValue().replaceAll("\\s+", "")
                                .replaceAll("（", "：").replaceAll("）", "").replaceAll("\\(", "：").replaceAll("\\)", "");
                        rowmessage.add(got_message);
                    } catch (Exception e) {
                        //System.out.println(e);
                    }
                }
                boxinfo.add(rowmessage);
            }
        }catch (Exception e){
            //System.out.println(e);
        }
        return boxinfo;
    }
    public static List<List>  get_gudongbox(XSSFWorkbook workbook, String sheetname){
        List<List> boxinfo=new ArrayList<List>();
        XSSFSheet sheet = workbook.getSheet(sheetname);
        int rowIndex;
        try{
            for (rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            List<String> rowmessage = new ArrayList<String>();
            XSSFRow row = sheet.getRow(rowIndex);
            if (row == null) {
                continue;
            }
            int columnNum = row.getLastCellNum();
            for (int columnIndex = 0; columnIndex < columnNum; columnIndex++) {
                try{
                    String got_message = row.getCell(columnIndex) == null ? "-" : row.getCell(columnIndex).getStringCellValue()
                            .replaceAll("（", "：").replaceAll("）", "").replaceAll("\\(", "：").replaceAll("\\)", "");
                    rowmessage.add(got_message);
                }catch (Exception e){
                    //System.out.println(e);
                    //rowmessage.add("-");
                }
            }
            boxinfo.add(rowmessage);
        }
//            List<String> rowmessage = new ArrayList<String>();
//            XSSFRow row = sheet.getRow(rowIndex);
//            int columnNum = row.getLastCellNum();
//            for (int columnIndex = 0; columnIndex < columnNum; columnIndex++) {
//                try{
//                    rowmessage.add(row.getCell(columnIndex).getStringCellValue());
//                }catch (Exception e){
//                    columnNum+=1;
//                    rowmessage.add("-");
//                }
//            }
//            boxinfo.add(rowmessage);
        }catch (Exception e){
            //System.out.println(e);
        }
        return boxinfo;
    }
}
