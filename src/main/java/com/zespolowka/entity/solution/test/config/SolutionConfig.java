package com.zespolowka.entity.solution.test.config;

import java.util.HashMap;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.simple.JSONObject;

/**
 * Created by Admin on 2016-04-17.
 */
@Data
@NoArgsConstructor
public class SolutionConfig {
	public static final String MAIN_FILE = "main_file";
	public static final String DEFAULT_RESTRICTED_LIST_PATH = "restrictedListPath";
	public static final String DEFAULT_COMPILER = "compiler";
	public static final String DEFAULT_INTERPRETER = "interpreter";
	public static final String COMPILER_FLAGS = "compilerFlags";
	public static final String INTERPRETER_FLAGS = "interpreterFlags";
	public static final String ULIMIT_FLAGS = "ulimitFlags";
	public static final String MAX_TIME_OF_TESTS = "maxTimeOfTests";
	public static final String DEFAULT_LANGUAGE = "language";
	public static final String DEFAULT_SOURCES = "sources";
	public static final String DEFAULT_TESTER_FILES = "testerFiles";
	public static final String DEFAULT_PREPARATIONS = "preparations";
	public static final String DEFAULT_TESTS = "tests";

	private String mainFile;
	private String restrictedListPath;
	private String compiler;
	private String interpreter;
	private String compilerFlags;
	private String interpreterFlags;
	private String ulimitFlags = "-t 200";
	private Integer maxTimeOfTests = 10;
	private String language;
	private String sources;
	private String testerFiles;
	private String preparations;
	private String tests;


	public JSONObject createJavaConfig(String sources, String tests, String restrictedListPath) {
		this.language = "java";
		this.sources = sources;
		this.tests = tests;
		this.compiler = "/usr/bin/javac";
		this.interpreter = "/usr/bin/java";
		this.compilerFlags = "-cp .:/var/www/Aluminium/Team_Programming_Rewritten/JAVA_LIB/*";
		this.interpreterFlags = "-Djava.security.manager -Djava.security.policy==/var/www/Aluminium/Team_Programming_Rewritten/JAVA_LIB/java.policy -cp .:/var/www/Aluminium/Team_Programming_Rewritten/JAVA_LIB/*";
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(DEFAULT_LANGUAGE, language);
		jsonObject.put(DEFAULT_SOURCES, sources);
		HashMap<String, String> map = new HashMap<>();
		map.put(DEFAULT_TESTS, tests);
		map.put(DEFAULT_PREPARATIONS, preparations);
		jsonObject.put(DEFAULT_TESTER_FILES, map);
		jsonObject.put(MAIN_FILE, mainFile);
		jsonObject.put(DEFAULT_RESTRICTED_LIST_PATH, restrictedListPath);
		jsonObject.put(DEFAULT_COMPILER, compiler);
		jsonObject.put(DEFAULT_INTERPRETER, interpreter);
		jsonObject.put(COMPILER_FLAGS, compilerFlags);
		jsonObject.put(INTERPRETER_FLAGS, interpreterFlags);
		jsonObject.put(ULIMIT_FLAGS, ulimitFlags);
		jsonObject.put(MAX_TIME_OF_TESTS, maxTimeOfTests);
		return jsonObject;
	}

	public JSONObject createCppConfig(String sources, String tests, String restrictedListPath, String compilerFlags) {
		this.language = "c++";
		this.sources = sources;
		this.tests = tests;
		this.compiler = "/usr/bin/c++";
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(DEFAULT_LANGUAGE, language);
		jsonObject.put(DEFAULT_SOURCES, sources);
		HashMap<String, String> map = new HashMap<>();
		map.put(DEFAULT_TESTS, tests);
		map.put(DEFAULT_PREPARATIONS, preparations);
		jsonObject.put(DEFAULT_TESTER_FILES, map);
		jsonObject.put(MAIN_FILE, mainFile);
		jsonObject.put(DEFAULT_RESTRICTED_LIST_PATH, restrictedListPath);
		jsonObject.put(DEFAULT_COMPILER, compiler);
		jsonObject.put(DEFAULT_INTERPRETER, interpreter);
		jsonObject.put(COMPILER_FLAGS, compilerFlags);
		jsonObject.put(INTERPRETER_FLAGS, interpreterFlags);
		jsonObject.put(ULIMIT_FLAGS, ulimitFlags);
		jsonObject.put(MAX_TIME_OF_TESTS, maxTimeOfTests);
		return jsonObject;
	}


	public JSONObject createPythonConfig(String sources, String tests, String restrictedListPath) {
		this.language = "python";
		this.sources = sources;
		this.tests = tests;
		this.interpreter = "/usr/bin/python3";
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(DEFAULT_LANGUAGE, language);
		jsonObject.put(DEFAULT_SOURCES, sources);
		HashMap<String, String> map = new HashMap<>();
		map.put(DEFAULT_TESTS, tests);
		map.put(DEFAULT_PREPARATIONS, preparations);
		jsonObject.put(DEFAULT_TESTER_FILES, map);
		jsonObject.put(MAIN_FILE, mainFile);
		jsonObject.put(DEFAULT_RESTRICTED_LIST_PATH, restrictedListPath);
		jsonObject.put(DEFAULT_COMPILER, compiler);
		jsonObject.put(DEFAULT_INTERPRETER, interpreter);
		jsonObject.put(COMPILER_FLAGS, compilerFlags);
		jsonObject.put(INTERPRETER_FLAGS, interpreterFlags);
		jsonObject.put(ULIMIT_FLAGS, ulimitFlags);
		jsonObject.put(MAX_TIME_OF_TESTS, maxTimeOfTests);
		return jsonObject;
	}

	public JSONObject createSqlConfig(String sources, String preparations, String tests, String restrictedListPath) {
		this.language = "sql";
		this.sources = sources;
		this.tests = tests;
		this.interpreter = "/usr/bin/python3";
		this.mainFile = "sql.py";
		JSONObject jsonObject = new JSONObject();
		jsonObject.put(DEFAULT_LANGUAGE, language);
		jsonObject.put(DEFAULT_SOURCES, sources);
		HashMap<String, String> map = new HashMap<>();
		map.put(DEFAULT_TESTS, tests);
		map.put(DEFAULT_PREPARATIONS, preparations);
		jsonObject.put(DEFAULT_TESTER_FILES, map);
		jsonObject.put(MAIN_FILE, mainFile);
		jsonObject.put(DEFAULT_RESTRICTED_LIST_PATH, restrictedListPath);
		jsonObject.put(DEFAULT_COMPILER, compiler);
		jsonObject.put(DEFAULT_INTERPRETER, interpreter);
		jsonObject.put(COMPILER_FLAGS, compilerFlags);
		jsonObject.put(INTERPRETER_FLAGS, interpreterFlags);
		jsonObject.put(ULIMIT_FLAGS, ulimitFlags);
		jsonObject.put(MAX_TIME_OF_TESTS, maxTimeOfTests);
		return jsonObject;
	}

}
