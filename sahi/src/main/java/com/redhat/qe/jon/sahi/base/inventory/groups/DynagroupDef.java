package com.redhat.qe.jon.sahi.base.inventory.groups;

/**
 * Class representing dynagroup definition.
 * @author fbrychta
 *
 */
public class DynagroupDef {
    private String name = null;
    private String description = null;
    private String providedExprName = null;
    private String expression = null;
    private boolean recursive = false;
    private int recalcIntMin = -1;
    
    public DynagroupDef(){
    }
    public DynagroupDef(String name,String description, String providedExprName,
            String expr, boolean recursive, int recalcIntMin){
        this.name = name;
        this.description = description;
        this.providedExprName = providedExprName;
        this.expression = expr;
        this.recursive = recursive;
        this.recalcIntMin = recalcIntMin;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getProvidedExprName() {
        return providedExprName;
    }
    public void setProvidedExprName(String providedExprName) {
        this.providedExprName = providedExprName;
    }
    public String getExpression() {
        return expression;
    }
    public void setExpression(String expression) {
        this.expression = expression;
    }
    public boolean isRecursive() {
        return recursive;
    }
    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }
    public int getRecalcInt() {
        return recalcIntMin;
    }
    public void setRecalcInt(int recalcInt) {
        this.recalcIntMin = recalcInt;
    }
}
