package persistence.dao;

import java.sql.*;

import java.util.ArrayList;
import java.util.List;

import filter.ResourceFilter;
import service.dto.*;

public class RecipeDAO {

	private static JDBCUtil jdbcUtil = null;

	public RecipeDAO() {
		jdbcUtil = new JDBCUtil();
	}

	public static int insertRecipe(Recipe rcp) {

		int result = 0;
		String sql = "INSERT INTO RECIPE (recipeId, recipeName, summary, nation, difficulty, image, report) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?)";
		Object[] param = new Object[] { rcp.getRecipeId(), rcp.getRecipeName(), rcp.getSummary(), rcp.getNation(),
				rcp.getDifficulty(), rcp.getImage(), rcp.getReport() };
		jdbcUtil.setSqlAndParameters(sql, param);

		try {
			result = jdbcUtil.executeUpdate();
			if (result == 1) {
				System.out.println("insert recipe success");
				insertRecipeIngredient(rcp.getIngList());
				insertRecipeStep(rcp.getStepList());
			}
			else
				System.out.println("insert recipe failed");
		} catch (Exception ex) {
			jdbcUtil.rollback();
			ex.printStackTrace();
		} finally {
			jdbcUtil.commit();
			jdbcUtil.close();
		}
		return result;
	}

	public static int insertRecipeStep(List<RecipeStep> rcpStepList) throws Exception {
		int result = 0;
		String sql = "INSERT INTO RECIPESTEP (recipeId, stepNum, content) " + "VALUES (?, ?, ?)";
		try {
			for (int i = 0; i < rcpStepList.size(); i++) {
				Object[] param = new Object[] { rcpStepList.get(i).getRecipeId(), rcpStepList.get(i).getStepNum(),
						rcpStepList.get(i).getContent() };
				jdbcUtil.setSqlAndParameters(sql, param);
				result += jdbcUtil.executeUpdate();
			}
			if (result == rcpStepList.size())
				System.out.println("insert recipeStep success");
			else 
				System.out.println("insert recipeStep failed");
		} catch (Exception e) {
			e.printStackTrace();
			jdbcUtil.rollback();
		} finally {
			jdbcUtil.commit();
			jdbcUtil.close();
		}
		return result;
	}

	public static int insertRecipeIngredient(List<RecipeIngredient> rcpIngList) throws Exception {
		int result = 0;
		String sql = "INSERT INTO RECIPESTEP (recipeId, ingredientId, ingredientName, amount, unit) "
				+ "VALUES (?, ?, ?, ?, ?)";

		try {
			for (int i = 0; i < rcpIngList.size(); i++) {
				Object[] param = new Object[] { rcpIngList.get(i).getRecipeId(), rcpIngList.get(i).getIngredientId(),
						rcpIngList.get(i).getIngredientName(), rcpIngList.get(i).getAmount(),
						rcpIngList.get(i).getUnit() };
				jdbcUtil.setSqlAndParameters(sql, param);
				result += jdbcUtil.executeUpdate();
			}
			if (result == rcpIngList.size())
				System.out.println("insert recipeIngredient success");
			else 
				System.out.println("insert recipeIngredient failed");
		} catch (Exception e) {
			e.printStackTrace();
			jdbcUtil.rollback();
		} finally {
			jdbcUtil.commit();
			jdbcUtil.close();
		}
		return result;
	}

	public static int updateRecipe(Recipe rcp) {

		String sql = "UPDATE RECIPE SET ";
		int index = 0;

		Object[] tempParam = new Object[10];

		if (rcp.getRecipeName() != null) { 
			sql += "recipeName = ?, "; 
			tempParam[index++] = rcp.getRecipeName(); 
		}
		if (rcp.getSummary() != null) { 
			sql += "summary = ?, ";
			tempParam[index++] = rcp.getSummary(); 
		}
		if (rcp.getNation() != null) { 
			sql += "nation = ?, "; 
			tempParam[index++] = rcp.getNation(); 
		}
		if (rcp.getDifficulty() != null) { 
			sql += "difficulty = ?, "; 
			tempParam[index++] = rcp.getDifficulty();
		}
		if (rcp.getImage() != null) { 
			sql += "image = ?, "; 
			tempParam[index++] = rcp.getImage();
		}

		sql += "WHERE recipeId = ? "; 
		sql = sql.replace(", WHERE", " WHERE"); 

		tempParam[index++] = rcp.getRecipeId(); 

		Object[] newParam = new Object[index];
		for (int i = 0; i < newParam.length; i++)
			newParam[i] = tempParam[i];

		jdbcUtil.setSqlAndParameters(sql, newParam);

		try {
			int result = jdbcUtil.executeUpdate(); 
			return result;
		} catch (Exception ex) {
			jdbcUtil.rollback();
			ex.printStackTrace();
		} finally {
			jdbcUtil.commit();
			jdbcUtil.close(); 
		}
		return 0;
	}

	public static int updateRecipeStep (List<RecipeStep> rcpStepList) {

		String sql = "UPDATE RECIPESTEP SET " + "SET STEPNUM=?, CONTENT=?" /*+  "WHERE RECIPEID=?"*/;
		int result = 0;
		try {
			for (RecipeStep rcpStep : rcpStepList) {
				Object[] param = new Object[] {rcpStep.getStepNum(), rcpStep.getContent()/*, rcpStep.getRecipeId() */};
				jdbcUtil.setSqlAndParameters(sql, param);
				result += jdbcUtil.executeUpdate();	
			}
			return result;		
		} catch (Exception ex) {
			jdbcUtil.rollback();
			ex.printStackTrace();
		}
		finally {
			jdbcUtil.commit();
			jdbcUtil.close();
		}		
		return 0;
	}

	public static int updateRecipeIngredient (List<RecipeIngredient> rcpIngList) {

		String sql = "UPDATE RECIPEINGREDIENT SET " + "SET INGREDIENTID=?, INGREDIENTNAME=?, AMOUNT=?, UNIT=?" /* + "WHERE RECIPEID=? "*/;
		int result = 0;
		try {
			for (RecipeIngredient rcpIng : rcpIngList) {
				Object[] param = new Object[] {rcpIng.getIngredientId(), rcpIng.getIngredientName(), rcpIng.getAmount(), rcpIng.getUnit()};
				jdbcUtil.setSqlAndParameters(sql, param);
				result += jdbcUtil.executeUpdate();	
			}
			return result;		
		} catch (Exception ex) {
			jdbcUtil.rollback();
			ex.printStackTrace();
		}
		finally {
			jdbcUtil.commit();
			jdbcUtil.close();
		}		
		return 0;
	}

	public static void deleteRecipe (String rcpId) {

		deleteRecipeStep(rcpId);
		deleteRecipeIngredient(rcpId);

		String sql = "DELETE FROM RECIPE WHERE recipeId = ?";
		jdbcUtil.setSqlAndParameters(sql, new Object[] {rcpId});

		try {
			int result = jdbcUtil.executeUpdate();
			if (result == 1)	System.out.println("delete recipe success");
			else	System.out.println("delete recipe failed");
		} catch (Exception ex) {
			jdbcUtil.rollback();
			jdbcUtil.close();
		}
	}

	public static void deleteRecipeStep (String recipeId) {
		String sql = "DELETE FROM RECIPESTEP WHERE recipeID = ?";
		jdbcUtil.setSqlAndParameters(sql, new Object[] {recipeId});

		try {
			int result = jdbcUtil.executeUpdate();
			if (result > 0)		System.out.println("delete recipeSteps success");
			else	System.out.println("delete recipeSteps failed");
		} catch (Exception ex) {
			jdbcUtil.rollback();
			jdbcUtil.close();
		}
	}

	public static void deleteRecipeIngredient (String recipeId) {
		String sql = "DELETE FROM RECIPE_INGREDIENT WHERE recipeId = ?";
		jdbcUtil.setSqlAndParameters(sql, new Object[] {recipeId});

		try {
			int result = jdbcUtil.executeUpdate();
			if (result > 0)		System.out.println("delete recipe_ingredient success");
			else	System.out.println("delete recipe_ingredient failed");
		} catch (Exception ex) {
			jdbcUtil.rollback();
			jdbcUtil.close(); 
		}
	}

	public static List<Recipe> getRecipeListByIngredient(List<Ingredient> ingredientList) {
		int size = ingredientList.size();

		String sql = "SELECT recipeId, recipeName, userId, summary, nation, difficulty, image, report "
				+ "FROM RECIPE r, RECIPE_INGREDIENT ri "
				+ "WHERE ingredientid = ? "; 

		for (int i = 0; i < ingredientList.size(); i++)
			sql += " or ingredientId = ? ";

		Object[] param = new Object[size];
		for (int i = 0; i < ingredientList.size(); i++)
			param[i++] = ingredientList.get(i++).getIngredientId();

		jdbcUtil.setSqlAndParameters(sql, param);

		try {
			ResultSet rs = jdbcUtil.executeQuery();		
			List<Recipe> rcpList = new ArrayList<Recipe>();
			while (rs.next()) {	
				Recipe rcp = new Recipe();
				rcp.setRecipeId(rs.getString("recipeId"));
				rcp.setRecipeName(rs.getString("recipeName"));
				rcp.setUserId(rs.getString("userId"));
				rcp.setSummary(rs.getString("summary"));
				rcp.setNation(rs.getString("nation"));
				rcp.setDifficulty(rs.getString("difficulty"));
				rcp.setImage(rs.getString("image"));
				rcp.setReport(rs.getInt("report"));
				rcpList.add(rcp);
			}
			if (rcpList.isEmpty())
				System.out.println("empty recipe");
			return rcpList;				
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			jdbcUtil.close();	
		}
		return null;
	}

	public static List<Recipe> getRecipeListByName(String recipeName) {
		String sql = "SELECT recipeId, recipeName, userId, summary, image " + "FROM RECIPE "
				+ "WHERE recipeName LIKE ? ";
		jdbcUtil.setSqlAndParameters(sql, new Object[] {  "%"+recipeName+"%" });

		try {
			ResultSet rs = jdbcUtil.executeQuery();
			List<Recipe> rcpList = new ArrayList<Recipe>();
			while (rs.next()) {
				Recipe rcp = new Recipe();
				rcp.setRecipeId(rs.getString("recipeId"));
				rcp.setRecipeName(rs.getString("recipeName"));
				rcp.setUserId(rs.getString("userId"));
				rcp.setSummary(rs.getString("summary"));
				rcp.setImage(rs.getString("image"));
				rcpList.add(rcp);
			}
			if (rcpList.isEmpty())
				System.out.println("empty refrigerator");
			return rcpList;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			jdbcUtil.close();
		}
		return null;
	}

	public static Recipe findRecipeById(String recipeId) {
		String sql = "SELECT recipeName, userId, summary, image " + "FROM RECIPE "
				+ "WHERE recipeId=? ";
		jdbcUtil.setSqlAndParameters(sql, new Object[] {recipeId});

		Recipe rcp = null;
		try {
			ResultSet rs = jdbcUtil.executeQuery();
			if (rs.next()) {
				rcp = new Recipe();
				rcp.setRecipeName(rs.getString("recipeName"));
				rcp.setUserId(rs.getString("userId"));
				rcp.setSummary(rs.getString("summary"));
				rcp.setImage(rs.getString("image"));
			}
			rcp.setIngList(findRcpIngById(recipeId));
			rcp.setStepList(findRcpStepById(recipeId));
			return rcp;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			jdbcUtil.close();
		}
		return null;
	}

	public static List<RecipeIngredient> findRcpIngById (String recipeId) {
		String sql = "SELECT ingredientName, amount, unit " + "FROM RECIPEINGREDIENT " + "WHERE recipeId=?";
		jdbcUtil.setSqlAndParameters(sql, new Object[] {recipeId});
		try {
			ResultSet rs = jdbcUtil.executeQuery();
			List<RecipeIngredient> rcpIngList = new ArrayList<RecipeIngredient>();
			while (rs.next()) {
				RecipeIngredient rcpIng = new RecipeIngredient();
				rcpIng.setIngredientName(rs.getString("ingredientName"));
				rcpIng.setAmount(Integer.parseInt(rs.getString("amount")));
				rcpIng.setUnit(rs.getString("unit"));
				rcpIngList.add(rcpIng);
			}
			return rcpIngList;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			jdbcUtil.close();
		}
		return null;
	}

	public static List<RecipeStep> findRcpStepById (String recipeId) {
		String sql = "SELECT stepNum, content " + "FROM RECIPESTEP " + "WHERE recipeId=?";
		jdbcUtil.setSqlAndParameters(sql, new Object[] {recipeId});
		RecipeStep rcpStep = null;
		try {
			ResultSet rs = jdbcUtil.executeQuery();
			List<RecipeStep> rcpStepList = new ArrayList<RecipeStep>();
			while (rs.next()) {
				rcpStep = new RecipeStep();
				rcpStep.setStepNum(Integer.parseInt(rs.getString("amount")));
				rcpStep.setContent(rs.getString("content"));
				rcpStepList.add(rcpStep);
			}
			return rcpStepList;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			jdbcUtil.close();
		}
		return null;
	}
}
