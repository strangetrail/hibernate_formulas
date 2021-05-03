package formulas;

import java.util.List;
import java.util.Set;

public class Symbol {
	public enum Fields {
		Id(0), TeX(1);
		
		private final int value;
	    private Fields(int value) {
	        this.value = value;
	    }

	    public int getValue() {
	        return value;
	    }
	};
	private int id;
	private String symbolTex;
	private List resultFormulas;
	private Set allFormulas;

	public Symbol () {}
	
	public Symbol (String symbol_tex)
	{
		this.symbolTex = symbol_tex;
	}
	
    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getSymbolTex()
    {
        return symbolTex;
    }

    public void setSymbolTex(String symbol_tex)
    {
        this.symbolTex = symbol_tex;
    }

    public List getResultFormulas()
    {
        return resultFormulas;
    }

    public void setResultFormulas(List formulas)
    {
        this.resultFormulas = formulas;
    }
    
    public Set getAllFormulas()
    {
    	return allFormulas;
    }
    
    public void setAllFormulas(Set formulas)
    {
    	this.allFormulas = formulas;
    }
    
    public boolean equals(Object obj)
    {
    	if (obj == null) return false;
        if (!this.getClass().equals(obj.getClass())) return false;

        Symbol obj2 = (Symbol)obj;
        if((this.id == obj2.getId()) && (this.symbolTex.equals(obj2.getSymbolTex()))) {
           return true;
        }
        return false;
    }
    
    public int hashCode()
    {
    	int tmp = 0;
    	tmp = (id + symbolTex).hashCode();
    	return tmp;
    }

}
