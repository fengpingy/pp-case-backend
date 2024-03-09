package com.pp.utils;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;


@Slf4j
public class CSVUtils {


    /**
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static List<String[]> getCsvData(File file) throws IOException {
        List<String[]> list = new ArrayList<>();
        int i = 0;
        try {
            CSVReader build = new CSVReaderBuilder(
                    new BufferedReader(new FileReader(file))
            ).build();
            for (String[] next : build) {
                if (i >= 1) {
                    list.add(next);
                }
                i++;
            }
            return list;
        } catch (Exception e) {
            return list;
        }
    }

    /**
     *
     * @param file
     * @param clazz
     * @return
     * @param <T>
     */
    public static <T> List<T> getCsvData2(File file, Class<T> clazz) {
        InputStreamReader in = null;
        CsvToBean<T> csvToBean = null;
        try {
            in = new InputStreamReader(Files.newInputStream(file.toPath()));
            HeaderColumnNameMappingStrategy<T> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(clazz);
            csvToBean = new CsvToBeanBuilder<T>(in).withMappingStrategy(strategy).build();
        } catch (Exception e) {
            log.error("数据转化失败");
            return null;
        }
        // todo 解析禅道原生csv带双引号文件会报错，第三方包问题待解决
        return csvToBean.parse();
    }



    /**
     * 写一行数据方法
     * @param row
     * @param csvWriter
     * @throws IOException
     */
    public static void writeRow(String[] row, BufferedWriter csvWriter) throws IOException {
        // 写入文件头部
        for (Object data : row) {
            StringBuffer sb = new StringBuffer();
            String rowStr = sb.append("\"").append(data).append("\",").toString();
            csvWriter.write(rowStr);
        }
        csvWriter.newLine();
    }

}