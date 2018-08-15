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

public class qa_1 {
    public static void main(String[] args){
        qa_main();
    }
    public static void qa_main(){
        Scanner sc=new Scanner(System.in);
        String question="1";
        try {
            Forest forest1= Library.makeForest("C:\\Users\\renyi\\IdeaProjects\\finance_knowledgegraph\\src\\output\\firm.dic");
            Forest forest2=Library.makeForest("C:\\Users\\renyi\\IdeaProjects\\finance_knowledgegraph\\src\\output\\indexdict.dic");
            Map<String,String> index_dir=makdict();
            while(!question.equals("0")){
                System.out.println("请输入问题（按0退出问答):");
                question=sc.next();
                String result=null;
                String sj=null;//subject
                String pt=null;//属性
                String index=null;
                String ind_pt=null;//指标的属性
                String tm=null;//年份
                String zb=null;//指标
                String sbj=null;
                if(!question.equals("0")){
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
                            if(natureStr.equals("t"))
                                tm=word;
                            if(natureStr.equals("zb"))
                                zb=word;
                            if(index_dir.containsKey(natureStr)){
                                index=index_dir.get(natureStr);
                                ind_pt=word;
                            }
                        }
                        try{
                            if(zb==null) {
                                sbj = sj + tm + index;
                                search_sp(sbj,ind_pt);
                            }
                            else{
                                sbj=sj+tm+zb;
                                search_index_data(sbj);
                            }


                        }catch (Exception e){
                            System.out.println("输入方式错误或找不到答案");
                        }
                    }
                    else{
                        for(int i =0;i<terms.size();i++){
                            String word = terms.get(i).getName(); //拿到词
                            String natureStr = terms.get(i).getNatureStr(); //拿到词性
                            if(natureStr.equals("gs") || natureStr.equals("nr"))//只能有一个公司
                                sj=word;
                            if(natureStr.equals("op") || natureStr.equals("dp"))//只能有一个公司
                                pt=word;
                        }
                        try{
                            search_sp(sj,pt);
                        }catch (Exception e){
                            System.out.println("输入方式错误或找不到答案");
                        }
                    }
                }
                else{
                    System.out.println("Bye!");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String search_sp(String subject,String property){
        tdb_tools tdbPersistence = new tdb_tools("D:\\finance_tdb");
        Model model=tdbPersistence.getModel("fiance2016");
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
        if (lens<=4){
            System.out.println("输入方式错误或找不到答案");
        }
        else{
            for(int i =3;i<lens-1;i++){
                System.out.println(ls[i].replaceAll(del1,""));
            }
        }
        queryExecution.close();
        return strout;
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
