package com.finance.question_answer;

import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.DicAnalysis;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import com.finance.tools.tdb_tools;
import com.finance.tools.basic_tools;
import org.nlpcn.commons.lang.tire.domain.Forest;
import org.nlpcn.commons.lang.tire.library.Library;

import java.util.*;

public class qa_2 {
    public static void main(String[] args){
    }
    public static String qa_main(String question){
        try {
            Forest forest1= Library.makeForest("C:\\Users\\renyi\\IdeaProjects\\finance_knowledgegraph\\src\\output\\firm.dic");
            Forest forest2=Library.makeForest("C:\\Users\\renyi\\IdeaProjects\\finance_knowledgegraph\\src\\output\\indexdict.dic");
            Map<String,String> index_dir=makdict();
            String result=null;
            String sj=null;//subject
            String pt=null;//属性
            String index=null;
            String ind_pt=null;
            String tm=null;//年份
            String resultstr=DicAnalysis.parse(question,forest1,forest2).toString();
            Result cut_result=DicAnalysis.parse(question,forest1,forest2);
            List<Term> terms=cut_result.getTerms();
            if (resultstr.contains("/t")){
                for(int i =0;i<terms.size();i++){
                    String word = terms.get(i).getName(); //拿到词
                    String natureStr = terms.get(i).getNatureStr(); //拿到词性
                    //System.out.println(natureStr);
                    if(natureStr.equals("gs"))//只能有一个公司
                        sj=word;
                    if(natureStr.equals("t"))//只能有一个公司
                        tm=word;
                    if(index_dir.containsKey(natureStr)){
                        index=index_dir.get(natureStr);
                        ind_pt=word;
                    }
                }
                try{
                    result=search_sp(sj+tm+index,ind_pt);
                    return result;
                }catch (Exception e){
                    System.out.println("输入方式错误或找不到答案");
                }
            }
            else{
                for(int i =0;i<terms.size();i++){
                    String word = terms.get(i).getName(); //拿到词
                    String natureStr = terms.get(i).getNatureStr(); //拿到词性
                    if(natureStr.equals("gs"))//只能有一个公司
                        sj=word;
                    if(natureStr.equals("op") || natureStr.equals("dp"))//只能有一个公司
                        pt=word;
                }
                try{
                    result=search_sp(sj,pt);
                    return result;
                }catch (Exception e){
                    System.out.println("输入方式错误或找不到答案");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "未找到";
    }
    public static String search_sp(String subject,String property){
        tdb_tools tdbPersistence = new tdb_tools("D:\\finance_tdb");
        Model model=tdbPersistence.getModel("fiance2016");
        String formatout="";
        String queryString="PREFIX source:<http://www.semanticweb.org/renyi/ontologies/2018/5/untitled-ontology-3#> " +
                String.format("SELECT ?object WHERE {source:%s source:%s ?object}",subject,property);
        //System.out.println(queryString);
        Query query = QueryFactory.create(queryString);
        QueryExecution queryExecution = QueryExecutionFactory.create(query, model);
        ResultSet resultSet = queryExecution.execSelect();

        String strout = ResultSetFormatter.asText(resultSet,query);
        String [] ls=strout.split("\n");
        List<String> names=new ArrayList<>();
        String del1="source:|\\s+|\\|";
        int lens=ls.length;
        for(int i =3;i<lens-1;i++){
            formatout+=ls[i].replaceAll(del1,"");
            formatout+='\n';
        }
        queryExecution.close();
        return formatout;
    }
    public static void search_index_data(String subject){
        tdb_tools tdbPersistence = new tdb_tools("D:\\finance_tdb");
        Model model=tdbPersistence.getModel("fiance2016");
        String queryString="PREFIX source:<http://www.semanticweb.org/renyi/ontologies/2018/5/untitled-ontology-3#> " +
                String.format("SELECT ?dp ?o WHERE {source:%s ?dp ?o}",subject);
        Query query = QueryFactory.create(queryString);
        QueryExecution queryExecution = QueryExecutionFactory.create(query, model);
        ResultSet resultSet = queryExecution.execSelect();

        String strout = ResultSetFormatter.asText(resultSet,query);
        System.out.println(strout);
//        String [] ls=strout.split("\n");
//        List<String> names=new ArrayList<>();
//        String del1="source:|\\s+|\\|";
//        int lens=ls.length;
//        for(int i =3;i<lens-1;i++){
//            System.out.println(ls[i].replaceAll(del1,""));
    }
    public static Map makdict(){
        Map<String,String> index_dict=new HashMap<String,String>();
        index_dict.put("yl","盈利能力");
        index_dict.put("czhai","偿债能力");
        index_dict.put("yy","营运情况");
        index_dict.put("czhang","成长情况");
        index_dict.put("gb","股本情况");
        index_dict.put("sy","非经常性损益");
        return index_dict;
    }
}
