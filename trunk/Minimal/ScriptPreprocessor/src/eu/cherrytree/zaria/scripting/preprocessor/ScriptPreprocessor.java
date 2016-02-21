/****************************************/
/* ScriptPreprocessor.java				*/
/* Created on: 07-Jun-2014				*/
/* Copyright Cherry Tree Studio 2014		*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.scripting.preprocessor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Objects;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.RhinoException;


/**
 * Preprocesses ZoneScript files. Gathers all imports and generates an output file
 * which includes all imported source code. Validates that the imports are at 
 * the beginning of the script. Filters out any duplicated imports.
 * 
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ScriptPreprocessor
{
	//--------------------------------------------------------------------------
	
	private static String scriptLocation;

	//--------------------------------------------------------------------------
		
	public static void main(String[] args)
	{
		if (args.length != 2)
		{
			System.err.println("Wrong amount of attributes!");
			System.err.println("[srcDir] [tgtDir]");
			return;
		}
		
		System.out.println("---------------SCRIPT PREPROCESSOR---------------");
		System.out.println("Preprocessing all zone script files.");
		System.out.println();
		
		int errors = processDir(new File(args[0]), new File(args[1]));

		if (errors > 0)
		{
			System.err.println();
			System.err.println("There were " + errors + " errors!");
			System.err.println("--------------PREPROCESSING FAILED---------------");
		}
		else
		{
			System.err.println();
			System.out.println("--------------PREPROCESSING FINISHED-------------");			
		}
	}		
	
	//--------------------------------------------------------------------------
	
	private static int processDir(File sourceFile, File targetFile)
	{		
		int errors = 0;
		
		if (sourceFile.isDirectory())
		{						
			targetFile.mkdirs();
			
			for (File nestedFile : sourceFile.listFiles())
				errors += processDir(nestedFile, new File(targetFile.getAbsolutePath() + "/" + nestedFile.getName()));						
		}
		else if (sourceFile.getAbsolutePath().toLowerCase().endsWith(".zonescript"))
		{
			try
			{
				System.out.println("Script: " + sourceFile.getAbsolutePath());
				
				System.out.print("Preprocessing.");
				String script = preProcess(sourceFile);
				
				System.out.print(" Validating.");
				validateScript(sourceFile.getName(), script);
				
				System.out.print(" Saving.");
				
				targetFile.createNewFile();				
				try (PrintWriter out = new PrintWriter(targetFile, "UTF-8"))
				{
					out.println(script);
				}
				
				System.out.println(" Done.");
				System.out.println();
			}
			catch(RhinoException | ScriptProcessorError | IOException ex)
			{
				System.err.println("There were errors during preprocessing:");
				System.err.println(ex.getMessage());
				
				errors += 1;
			}									
		}
		
		return errors;
	}
	
	//--------------------------------------------------------------------------

	public static void setScriptLocation(String scriptLocation)
	{
		ScriptPreprocessor.scriptLocation = scriptLocation;
	}
			
	//--------------------------------------------------------------------------
	
	private static String loadFileAsString(File file) throws IOException
	{		
		byte[] bytes;
		
		try (InputStream in = new FileInputStream(file))
		{
			long length = file.length();
			
			if (length > Integer.MAX_VALUE)
				throw new IOException("File is too large!");
			
			bytes = new byte[(int) length];
			int offset = 0;
			int numRead = 0;
			
			while (offset < bytes.length && (numRead = in.read(bytes, offset, bytes.length - offset)) >= 0)
				offset += numRead;
			
			if (offset < bytes.length)
			   throw new IOException("Could not completely read file " + file);
		}

		return new String(bytes);
	}
	
	//--------------------------------------------------------------------------

	private static class ImportInfo
	{
		private String source;
		private String target;
		private String whiteSpace;
		private int offset;
		private int lineNumber = -1;

		public ImportInfo(String source, int offset)
		{
			this.source = source;
			this.target = removeComment(source.substring(source.indexOf("import") + 7, source.length() - 1).trim());
			this.offset = offset;

			StringBuilder str = new StringBuilder();

			for (int i = 0; i < source.length(); i++)
			{
				str.append(" ");
			}

			whiteSpace = str.toString();

			if (!isValid(target))
			{
				target = null;
			}
		}

		public ImportInfo(File file)
		{
			String path = file.getAbsolutePath();
			
			path = path.substring(scriptLocation.length());
			path = path.substring(0, path.length() - 11);
			
			target = path.replace(File.separator, ".");
			source = "import " + target + ";";	
		}
				
		private boolean isValid(String source)
		{
			char[] chars = source.toCharArray();

			for (int i = 0; i < chars.length; i++)
			{
				char c = chars[i];

				if (c == '*')
				{
					if (i != chars.length - 1)
					{
						return false;
					}
				}
				else if (c == '.')
				{
					if (i == 0 || i == chars.length - 1)
					{
						return false;
					}
				}
				else if (!Character.isLetter(c) && !Character.isDigit(c))
				{
					return false;
				}
			}


			return true;
		}

		private String removeComment(String line)
		{
			char[] chars = line.toCharArray();
			boolean comment = false;

			StringBuilder lineBuilder = new StringBuilder();

			for (int i = 0; i < chars.length; i++)
			{
				if (comment)
				{
					lineBuilder.append(" ");

					if (chars[i] == '*')
					{
						if (i < chars.length - 1)
						{
							if (chars[i + 1] == '/')
							{
								i++;
								lineBuilder.append(" ");
								comment = false;
							}
						}
					}
				}
				else
				{
					if (chars[i] == '/' && i < chars.length - 1 && chars[i + 1] == '*')
					{
						i++;
						lineBuilder.append("  ");
						comment = true;
					}
					else
					{
						lineBuilder.append(chars[i]);
					}
				}
			}

			return lineBuilder.toString().trim();
		}
		
		public void setLineNumber(String[] lines)
		{
			int count = 0;

			for (int i = 0; i < lines.length; i++)
			{
				count += lines.length;

				if (count >= offset)
				{
					lineNumber = i;
					return;
				}
			}
		}

		public int getLineNumber()
		{
			return lineNumber;
		}
				
		public String getSource()
		{
			return source;
		}

		public String getTarget()
		{
			return target;
		}

		public int getOffset()
		{
			return offset;
		}

		public String getWhiteSpace()
		{
			return whiteSpace;
		}

		@Override
		public boolean equals(Object obj)
		{
			if(obj instanceof ImportInfo)
			{
				ImportInfo info = (ImportInfo) obj;
												
				return info.target.equals(this.target);
			}
			else
				return false;
		}				

		@Override
		public int hashCode()
		{
			int hash = 7;
			hash = 43 * hash + Objects.hashCode(this.target);
			return hash;
		}
	}
	
	//--------------------------------------------------------------------------
	
	private static class ImportSource
	{
		private String source;
		private String importName;

		public ImportSource(String source, String importName)
		{
			this.source = source.trim();
			this.importName = importName.trim();
		}

		public String getSource()
		{
			return source;
		}

		public String getImportName()
		{
			return importName;
		}				
	}
	
	//--------------------------------------------------------------------------
	
	public static abstract class ScriptError extends Error
	{
		public ScriptError(String scriptName)
		{
			super("There was error preprocessing script " + scriptName);
		}
		
		public ScriptError(String scriptName, Throwable cause)
		{
			super("There was error preprocessing script " + scriptName, cause);
		}
	}
	
	//--------------------------------------------------------------------------
	
	public static class ScriptValidationError extends ScriptError
	{
		private String message;
		
		public ScriptValidationError(RhinoException rhinoException)
		{
			super(rhinoException.sourceName());
			
			message = createMessage(rhinoException);
		}

		public ScriptValidationError(RhinoException rhinoException, Throwable cause)
		{
			super(rhinoException.sourceName(), cause);
			
			message = createMessage(rhinoException);
		}		
		
		private String createMessage(RhinoException rhinoException)
		{
			StringBuilder errorBuilder = new StringBuilder();
			
			errorBuilder.append("Validation of script ").append(rhinoException.sourceName()).append(" failed.\n[")
						.append(rhinoException.lineNumber()).append("]: \"")
						.append(rhinoException.lineSource()).append("\" ; Error ").append(rhinoException.details());
			
			return errorBuilder.toString();
		}

		@Override
		public String getMessage()
		{
			return message;
		}				
	}

	//--------------------------------------------------------------------------
	
	public static class ScriptProcessorError extends ScriptError
	{
		public static class Info
		{
			private String details;
			private String lineSource;
			private int offset;
			private int lineNumber;

			public Info(String details, String lineSource, int lineNumber, int offset)
			{
				this.details = details;
				this.lineSource = lineSource;
				this.offset = offset;
				this.lineNumber = lineNumber;
			}

			public String getDetails()
			{
				return details;
			}

			public String getLineSource()
			{
				return lineSource;
			}

			public int getOffset()
			{
				return offset;
			}

			public int getLineNumber()
			{
				return lineNumber;
			}
		}
		
		private ArrayList<Info> info;

		public ScriptProcessorError(Info info, String scriptName)
		{
			super( scriptName);

			this.info = new ArrayList<>();
			this.info.add(info);
		}

		public ScriptProcessorError(Info info, String scriptName, Throwable throwable)
		{
			super(scriptName, throwable);

			this.info = new ArrayList<>();
			this.info.add(info);
		}

		public ScriptProcessorError(ArrayList<Info> info, String scriptName)
		{
			super(scriptName);

			this.info = info;
		}

		public ScriptProcessorError(ArrayList<Info> info, String scriptName, Throwable throwable)
		{
			super(scriptName, throwable);

			this.info = info;
		}

		public ArrayList<Info> getInfo()
		{
			return info;
		}

		@Override
		public String getMessage()
		{
			StringBuilder msg = new StringBuilder();

			msg.append(super.getMessage()).append("\n");

			if (getCause() != null)
			{
				msg.append("Cause:").append(getCause().getMessage());
			}

			for (Info inf : info)
			{
				msg.append("[").append(inf.getLineNumber()).append("]: \"").append(inf.getLineSource()).append("\" ; Error ").append(inf.getDetails()).append("\n");
			}

			return msg.toString();
		}
	}

	//--------------------------------------------------------------------------
	
	private enum ParseState
	{
		LINE_COMMENT,
		COMMENT,
		SOURCE,
		WHITESPACE,
		IMPORT;
	}

	//--------------------------------------------------------------------------
	
	private static ArrayList<ImportInfo> getImports(String source)
	{
		char[] chars = source.toCharArray();
		ArrayList<ImportInfo> imports = new ArrayList<>();
		StringBuilder importBuilder = new StringBuilder();

		ParseState state = ParseState.WHITESPACE;

		int index;

		for (index = 0; index < chars.length; index++)
		{
			switch (state)
			{
				case SOURCE:
				case WHITESPACE:

					state = ParseState.SOURCE;

					if (chars[index] == '/')
					{
						if (index < chars.length - 1)
						{
							if (chars[index + 1] == '/')
							{
								state = ParseState.LINE_COMMENT;
								index++;
							}
							else if (chars[index + 1] == '*')
							{
								state = ParseState.COMMENT;
								index++;
							}
						}
					}
					else if (chars[index] == 'i')
					{
						if (index < chars.length - 6)
						{
							if (chars[index + 1] == 'm' && chars[index + 2] == 'p' && chars[index + 3] == 'o' && chars[index + 4] == 'r' && chars[index + 5] == 't')
							{
								state = ParseState.IMPORT;
								importBuilder.append("import");
								index += 5;
							}
						}
					}
					else if (Character.isWhitespace(chars[index]))
					{
						state = ParseState.WHITESPACE;
					}

					break;

				case COMMENT:

					if (chars[index] == '*')
					{
						if (index < chars.length - 1)
						{
							if (chars[index + 1] == '/')
							{
								state = ParseState.WHITESPACE;
								index++;
							}
						}
					}

					break;

				case LINE_COMMENT:

					if (chars[index] == '\n')
					{
						state = ParseState.WHITESPACE;
					}

					break;

				case IMPORT:

					if (chars[index] == ';')
					{
						state = ParseState.WHITESPACE;

						importBuilder.append(chars[index]);
						imports.add(new ImportInfo(importBuilder.toString(), index - importBuilder.length() + 1));
						importBuilder.delete(0, importBuilder.length());
					}
					else if (chars[index] == '\n')
					{
						state = ParseState.SOURCE;
					}
					else if (chars[index] == '/' && index < chars.length - 1 && chars[index + 1] == '/')
					{
						state = ParseState.LINE_COMMENT;
						index++;
						importBuilder.delete(0, importBuilder.length());
					}
					else
					{
						importBuilder.append(chars[index]);
					}

					break;
			}

			if (state == ParseState.SOURCE)
			{
				break;
			}
		}

		return imports;
	}

	//--------------------------------------------------------------------------
	
	private static String getImportSource(String scriptName, ImportInfo scriptFakeInport, ArrayList<ImportInfo> imports, String[] lines) throws ScriptProcessorError
	{
		ArrayList<ImportSource> sources = new ArrayList<>();
		ArrayList<ScriptProcessorError.Info> error_info = new ArrayList<>();
		ArrayList<ImportInfo> checkedImports = new ArrayList<>();
		
		checkedImports.add(scriptFakeInport);
		
		for (ImportInfo info : imports)
		{
			info.setLineNumber(lines);

			try
			{
				ArrayList<ImportSource> localSources = getImportSources(checkedImports, info, info);

				for(ImportSource src : localSources)
				{
					try
					{
						validateScript(src.getImportName(), src.getSource());
					}
					catch (EvaluatorException | EcmaError ex)
					{
						error_info.add(new ScriptProcessorError.Info(ex.details(), info.getSource(), info.getLineNumber(), info.getOffset()));
					}
				}

				sources.addAll(localSources);
			}
			catch (ScriptProcessorError ex)
			{
				error_info.addAll(ex.getInfo());
			}				
		}				
		
		if(!error_info.isEmpty())
			throw new ScriptProcessorError(error_info, scriptName);
		
		StringBuilder sourceBuilder = new StringBuilder();
		
		sourceBuilder.append("\n// ----- IMPORTS_BEGIN ----- \n\n");
		
		for(ImportSource src : sources)
		{
			sourceBuilder.append("// Imported from ").append(src.getImportName()).append("\n\n");
			sourceBuilder.append(src.getSource()).append("\n\n");
			sourceBuilder.append("// ").append(src.getImportName()).append("\n\n");
		}
		
		sourceBuilder.append("// ----- IMPORTS_END ----- \n");
		
		return sourceBuilder.toString();
	}

	//--------------------------------------------------------------------------

	private static ArrayList<ImportSource> getImportSources(ArrayList<ImportInfo> checkedImports, ImportInfo importInfo, ImportInfo topImportInfo) throws ScriptProcessorError
	{		
		if(checkedImports.contains(importInfo))
			return new ArrayList<>();
		
		String path = scriptLocation + importInfo.getTarget().replace(".", File.separator) + ".zonescript";
		
		try
		{							
			ArrayList<ImportInfo> imports = new ArrayList<>();
			ArrayList<ImportInfo> importsTmp = new ArrayList<>();
			
			ArrayList<ImportSource> sources = new ArrayList<>();
			
			sources.add(new ImportSource(getSourceAndImports(path, imports), importInfo.getTarget()));
			
			importsTmp.addAll(imports);
			
			for(ImportInfo info : importsTmp)
			{
				if(checkedImports.contains(info))
					imports.remove(info);
				else
					checkedImports.add(info);
			}
			
			checkedImports.add(importInfo);
			
			for(ImportInfo info : imports)
				sources.addAll(getImportSources(checkedImports, info, topImportInfo));						
			
			return sources;			
		}
		catch (ScriptProcessorError | IOException ex)
		{
			throw new ScriptProcessorError(new ScriptProcessorError.Info(ex.getMessage(), topImportInfo.getSource(), topImportInfo.getLineNumber(), topImportInfo.getOffset()), new File(path).getName());
		}
	}
	
	//--------------------------------------------------------------------------

	public static void validateScript(String name, String source) throws RhinoException
	{
		Context context = Context.enter();
		context.compileString(source, name, 1, null);
		
		Context.exit();
	}
	
	//--------------------------------------------------------------------------
	
	private static String getSourceAndImports(String path, ArrayList<ImportInfo> imports) throws ScriptProcessorError, IOException
	{
		File file = new File(path);
		
		String source = loadFileAsString(file);

		return parseForImports(source, file.getName(), imports, true);
	}

	//--------------------------------------------------------------------------
	
	private static String parseForImports(String source, String scriptName, ArrayList<ImportInfo> imports, boolean collapseImports) throws ScriptProcessorError
	{
		String[] lines = source.split("\n");
		imports.addAll(getImports(source));
		ArrayList<ScriptProcessorError.Info> error_info = new ArrayList<>();

		for (ImportInfo info : imports)
		{
			if (info.getTarget() == null)
			{
				info.setLineNumber(lines);
				error_info.add(new ScriptProcessorError.Info("Invalid import.", info.getSource(), info.getLineNumber(), info.getOffset()));
			}
			else if (!info.getTarget().endsWith("*"))
			{
				File file = new File(scriptLocation + info.getTarget().replace(".", File.separator) + ".zonescript");

				if (!file.exists())
				{
					info.setLineNumber(lines);
					error_info.add(new ScriptProcessorError.Info("Can't find script " + file.getPath(), info.getSource(), info.getLineNumber(), info.getOffset()));
				}
			}
		}

		if (!error_info.isEmpty())
		{
			throw new ScriptProcessorError(error_info, scriptName);
		}

		StringBuilder sourceBuilder = new StringBuilder(source);

		for (ImportInfo info : imports)
		{
			if(collapseImports)
			{
				int offset = sourceBuilder.indexOf(info.getSource());				
				sourceBuilder.replace(offset, offset + info.getSource().length(), "");
			}
			else
			{
				sourceBuilder.replace(info.getOffset(), info.getOffset() + info.getSource().length(), info.getWhiteSpace());
			}
		}

		return sourceBuilder.toString();
	}

	//--------------------------------------------------------------------------
	
	public static String preProcess(File sourceFile) throws ScriptProcessorError, IOException
	{
		String source = loadFileAsString(sourceFile);
				
		String[] lines = source.split("\n");
		ArrayList<ImportInfo> imports = getImports(source);
		ArrayList<ScriptProcessorError.Info> error_info = new ArrayList<>();

		for (ImportInfo info : imports)
		{
			System.out.println("Processing import: " + info.getSource());
			
			if (info.getTarget() == null)
			{
				info.setLineNumber(lines);
				error_info.add(new ScriptProcessorError.Info("Invalid import.", info.getSource(), info.getLineNumber(), info.getOffset()));
			}
			else if (!info.getTarget().endsWith("*"))
			{
				File file = new File(scriptLocation + info.getTarget().replace(".", File.separator) + ".zonescript");

				if (!file.exists())
				{
					info.setLineNumber(lines);
					error_info.add(new ScriptProcessorError.Info("Can't find script " + file.getPath(), info.getSource(), info.getLineNumber(), info.getOffset()));
				}
			}
		}

		if (!error_info.isEmpty())
			throw new ScriptProcessorError(error_info, sourceFile.getName());

		StringBuilder sourceBuilder = new StringBuilder(source);

		if (!imports.isEmpty())
		{
			for (ImportInfo info : imports)
			{
				int offset = sourceBuilder.indexOf(info.getSource());				
				sourceBuilder.replace(offset, offset + info.getSource().length(), "");
			}

			sourceBuilder.insert(imports.get(0).getOffset(), getImportSource(sourceFile.getName(), new ImportInfo(sourceFile), imports, lines));
		}

		return sourceBuilder.toString();
	}

	//--------------------------------------------------------------------------
	
	public static String prepareForParsing(String source, String scriptName) throws ScriptProcessorError
	{
		return parseForImports(source, scriptName, new ArrayList<ImportInfo>(), false);
	}
	
	//--------------------------------------------------------------------------
}
