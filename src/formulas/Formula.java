package formulas;

import java.util.*;

public class Formula {
	public enum Fields {
		Id(0), TeX(1), Page(2), Result(3);
		
		private final int value;
	    private Fields(int value) {
	        this.value = value;
	    }

	    public int getValue() {
	        return value;
	    }
	};
	
    private int id;
    private String formulaTex;
    private int pageNum;
    private Integer resultSymbol;
    private Set symbols;
    
    public Formula() {}
    
    public Formula(String tex, int page_num)
    {
        this.formulaTex = tex;
        this.pageNum = page_num;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getFormulaTex()
    {
        return formulaTex;
    }

    public void setFormulaTex(String formula_tex)
    {
        this.formulaTex = formula_tex;
    }

    public int getPageNum()
    {
        return pageNum;
    }

    public void setPageNum(int page_num)
    {
        this.pageNum = page_num;
    }


    public Set getSymbols()
    {
        return symbols;
    }

    public void setSymbols(Set symbols)
    {
        this.symbols = symbols;
    }

    public Integer getResultSymbol()
    {
        return resultSymbol;
    }

    public void setResultSymbol(Integer result_symbol)
    {
        this.resultSymbol = result_symbol;
    }

}
