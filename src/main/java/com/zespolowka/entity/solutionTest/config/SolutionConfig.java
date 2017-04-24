package com.zespolowka.entity.solutionTest.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.simple.JSONObject;

import java.util.HashMap;

/**
 * Created by Admin on 2016-04-17.
 */
@Data
@NoArgsConstructor
public class SolutionConfig {
    public static final String MAIN_FILE = "main_file";
    public static final String RESTRICTED_LIST_PATH = "restricted_list_path";
    public static final String COMPILER = "compiler";
    public static final String INTERPRETER = "interpreter";
    public static final String COMPILER_FLAGS = "compiler_flags";
    public static final String INTERPRETER_FLAGS = "interpreter_flags";
    public static final String ULIMIT_FLAGS = "ulimit_flags";
    public static final String MAX_TIME_OF_TESTS = "max_time_of_tests";
    public static final String LANGUAGE = "language";
    public static final String SOURCES = "sources";
    public static final String TESTER_FILES = "tester_files";
    public static final String PREPARATIONS = "preparations";
    public static final String TESTS = "tests";

    private String mainFile;
    private String restricted_list_path;
    private String compiler;
    private String interpreter;
    private String compiler_flags;
    private String interpreter_flags;
    private String ulimit_flags = "-t 200";
    private Integer max_time_of_tests = 10;
    private String language;
    private String sources;
    private String tester_files;
    private String preparations;
    private String tests;


    public JSONObject createJavaConfig(String sources, String tests, String restricted_list_path) {
        this.language = "java";
        this.sources = sources;
        this.tests = tests;
        this.compiler = "/usr/bin/javac";
        this.interpreter = "/usr/bin/java";
        this.compiler_flags = "-cp .:/var/www/Aluminium/Team_Programming_Rewritten/JAVA_LIB/*";
        this.interpreter_flags = "-Djava.security.manager -Djava.security.policy==/var/www/Aluminium/Team_Programming_Rewritten/JAVA_LIB/java.policy -cp .:/var/www/Aluminium/Team_Programming_Rewritten/JAVA_LIB/*";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(LANGUAGE, language);
        jsonObject.put(SOURCES, sources);
        HashMap<String, String> map = new HashMap<>();
        map.put(TESTS, tests);
        map.put(PREPARATIONS, preparations);
        jsonObject.put(TESTER_FILES, map);
        jsonObject.put(MAIN_FILE, mainFile);
        jsonObject.put(RESTRICTED_LIST_PATH, restricted_list_path);
        jsonObject.put(COMPILER, compiler);
        jsonObject.put(INTERPRETER, interpreter);
        jsonObject.put(COMPILER_FLAGS, compiler_flags);
        jsonObject.put(INTERPRETER_FLAGS, interpreter_flags);
        jsonObject.put(ULIMIT_FLAGS, ulimit_flags);
        jsonObject.put(MAX_TIME_OF_TESTS, max_time_of_tests);
        return jsonObject;
    }

    public JSONObject createCppConfig(String sources, String tests, String restricted_list_path, String compiler_flags) {
        this.language = "c++";
        this.sources = sources;
        this.tests = tests;
        this.compiler = "/usr/bin/c++";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(LANGUAGE, language);
        jsonObject.put(SOURCES, sources);
        HashMap<String, String> map = new HashMap<>();
        map.put(TESTS, tests);
        map.put(PREPARATIONS, preparations);
        jsonObject.put(TESTER_FILES, map);
        jsonObject.put(MAIN_FILE, mainFile);
        jsonObject.put(RESTRICTED_LIST_PATH, restricted_list_path);
        jsonObject.put(COMPILER, compiler);
        jsonObject.put(INTERPRETER, interpreter);
        jsonObject.put(COMPILER_FLAGS, compiler_flags);
        jsonObject.put(INTERPRETER_FLAGS, interpreter_flags);
        jsonObject.put(ULIMIT_FLAGS, ulimit_flags);
        jsonObject.put(MAX_TIME_OF_TESTS, max_time_of_tests);
        return jsonObject;
    }


    public JSONObject createPythonConfig(String sources, String tests, String restricted_list_path) {
        this.language = "python";
        this.sources = sources;
        this.tests = tests;
        this.interpreter = "/usr/bin/python3";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(LANGUAGE, language);
        jsonObject.put(SOURCES, sources);
        HashMap<String, String> map = new HashMap<>();
        map.put(TESTS, tests);
        map.put(PREPARATIONS, preparations);
        jsonObject.put(TESTER_FILES, map);
        jsonObject.put(MAIN_FILE, mainFile);
        jsonObject.put(RESTRICTED_LIST_PATH, restricted_list_path);
        jsonObject.put(COMPILER, compiler);
        jsonObject.put(INTERPRETER, interpreter);
        jsonObject.put(COMPILER_FLAGS, compiler_flags);
        jsonObject.put(INTERPRETER_FLAGS, interpreter_flags);
        jsonObject.put(ULIMIT_FLAGS, ulimit_flags);
        jsonObject.put(MAX_TIME_OF_TESTS, max_time_of_tests);
        return jsonObject;
    }

    public JSONObject createSqlConfig(String sources, String preparations, String tests, String restricted_list_path) {
        this.language = "sql";
        this.sources = sources;
        this.tests = tests;
        this.interpreter = "/usr/bin/python3";
        this.mainFile = "sql.py";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(LANGUAGE, language);
        jsonObject.put(SOURCES, sources);
        HashMap<String, String> map = new HashMap<>();
        map.put(TESTS, tests);
        map.put(PREPARATIONS, preparations);
        jsonObject.put(TESTER_FILES, map);
        jsonObject.put(MAIN_FILE, mainFile);
        jsonObject.put(RESTRICTED_LIST_PATH, restricted_list_path);
        jsonObject.put(COMPILER, compiler);
        jsonObject.put(INTERPRETER, interpreter);
        jsonObject.put(COMPILER_FLAGS, compiler_flags);
        jsonObject.put(INTERPRETER_FLAGS, interpreter_flags);
        jsonObject.put(ULIMIT_FLAGS, ulimit_flags);
        jsonObject.put(MAX_TIME_OF_TESTS, max_time_of_tests);
        return jsonObject;
    }

}
