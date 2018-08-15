package com.finance.tools;
import com.finance.tools.excel_tools;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class firm_name_tools {
    public static void main(String[] args){
        String file_name_path="C:\\Users\\renyi\\IdeaProjects\\finance_knowledgegraph\\src\\output\\firms.txt";
        String dict_path="C:\\Users\\renyi\\IdeaProjects\\finance_knowledgegraph\\src\\output\\firm.dic";
        create_dic(file_name_path,dict_path);
        //read_file_names(file_name_path);
    }
    public static void create_dic(String file_name_path,String dict_path){
        ArrayList<String> names=read_file_names(file_name_path);
        File outfile=new File(dict_path);
        if(!outfile.exists()) {
            try {
                outfile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Iterator<String > nameiter=names.iterator();
        try {
            FileWriter fw=new FileWriter(outfile);
            while(nameiter.hasNext()){
                String name=nameiter.next();
                System.out.println(name);
                fw.write(name+'\t'+"gs\t1000\n");
            }
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static Set get_file_names(String filepath){
        String name=new String();
        Set<String> nameset= new HashSet<>();
        ArrayList<String> files=excel_tools.getFiles(filepath);
        Pattern pattern = Pattern.compile("\\d+wordextra\\\\\\d+(.*)：");
        for (int i=0;i<files.size();i++){
            Matcher matcher = pattern.matcher(files.get(i));
            while (matcher.find()) {
                name=matcher.group(1);
                //System.out.println(find);
            }
            nameset.add(name);
        }
        return nameset;
    }
    public static void save_file_names(String savepath){
        File outfile=new File(savepath);
        if(!outfile.exists()) {
            try {
                outfile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String file_path="C:\\Users\\renyi\\IdeaProjects\\finance_knowledgegraph\\src\\data\\2016wordextra";
        Set names=get_file_names(file_path);
        Iterator<String > nameiter=names.iterator();
        try {
            FileWriter fw=new FileWriter(outfile);
            while(nameiter.hasNext()){
                String name=nameiter.next();
                System.out.println(name);
                fw.write(name+'\n');
            }
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static ArrayList<String> read_file_names(String file_name_path){
        ArrayList<String> file_names=new ArrayList<>();
        try {
            FileReader fr=new FileReader(new File(file_name_path));
            BufferedReader br = new BufferedReader(fr);
            String line=null;
            while((line=br.readLine())!=null){
                file_names.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(file_names);
        System.out.println(file_names.size());
        return file_names;
    }
}
