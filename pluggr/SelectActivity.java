package com.techomite.math.pluggr;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class SelectActivity extends AppCompatActivity {

    private ExpandableListView listView;
    private ExpandableListAdapter listAdapter;
    private ArrayList<String> listHeader;
    private HashMap<String, ArrayList<String>> listHashMap;
    private static String[][] equationStorage;
    private static ArrayList<String> cust = new ArrayList<>();;
    private static String[] custEq = new String[cust.size()];;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Equation Select");

        if (readData("custom_storage").toString().length() > 0) {
            cust = (ArrayList<String>) readData("custom_storage");
            custEq = (String[]) readData("custom_equation_storage");

        }
        equationStorage = new String[6][];
        listView = (ExpandableListView) findViewById(R.id.eLV);
        inputData();
        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Intent intent = new Intent(SelectActivity.this, MainActivity.class);
                intent.putExtra("com.example.stevenyu.pluggr.SET_EQUATION", equationStorage[groupPosition][childPosition]);
                startActivity(intent);
                return true;
            }
        });
        listAdapter = new ExpandableListAdapter(this, listHeader, listHashMap);
        listView.setAdapter(listAdapter);
    }

    private void inputData() {
        listHeader = new ArrayList<>();
        listHashMap = new HashMap<>();

        listHeader.add("Algebra");
        listHeader.add("Geometry");
        listHeader.add("Statistics");
        listHeader.add("Chemistry");
        listHeader.add("Physics");
        listHeader.add("Custom");

        ArrayList<String> alg = new ArrayList<>();
        alg.add("Compound Interest");
        alg.add("Cubic Formula");
        alg.add("Distance Formula");
        alg.add("Point-Slope Formula");
        alg.add("Pythagorean Theorem");
        alg.add("Quadratic Formula");
        alg.add("Slope Formula");
        alg.add("Slope-Intercept Formula");
        alg.add("Standard Line Formula");
        String[] algEq = new String[alg.size()];
        algEq[0] = "A=P(1+r/n)^(nt)";
        algEq[1] = "ax^3+bx^2+cx+d=0";
        algEq[2] = "d=sqrt(([x₂]-[x₁])^2+([y₂]-[y₁])^2)";
        algEq[3] = "y-[y₁]=m(x-[x₁])";
        algEq[4] = "a^2+b^2=c^2";
        algEq[5] = "ax^2+bx+c=0";
        algEq[6] = "m=([y₂]-[y₁])/([x₂]-[x₁])";
        algEq[7] = "y=mx+b";
        algEq[8] = "Ax+By=C";
        equationStorage[0] = algEq;

        ArrayList<String> geo = new ArrayList<>();
        geo.add("Angle: Radians-Degrees");
        geo.add("Area of Circle");
        geo.add("Area of Regular Polygon");
        geo.add("Area of Triangle");
        geo.add("Area of Trapezoid");
        geo.add("Regular Polygon Angle Formula");
        geo.add("Surface Area of Cylinder");
        geo.add("Surface Area of Right Cone");
        geo.add("Surface Area of Right Cone Frustum");
        geo.add("Surface Area of Sphere");
        geo.add("Volume of Cylinder");
        geo.add("Volume of Right Cone");
        geo.add("Volume of Right Cone Frustum");
        geo.add("Volume of Sphere");
        geo.add("Volume of Square Pyramid");
        String[] geoEq = new String[geo.size()];
        geoEq[0] = "[Rd]=[D°]*π/180";
        geoEq[1] = "A=πr^2";
        geoEq[2] = "A=ap/2";
        geoEq[3] = "A=1/2bh";
        geoEq[4] = "A=([b₁]+[b₂])/2*h";
        geoEq[5] = "[Θ]=180(n-2)/n";
        geoEq[6] = "S=2*πrh+2*πr^2";
        geoEq[7] = "S=πrsqrt(r^2+h^2)";
        geoEq[8] = "S=πs(R+r)";
        geoEq[9] = "S=4*πr^2";
        geoEq[10] = "V=πr^2h";
        geoEq[11] = "V=πr^2h/3";
        geoEq[12] = "V=π(r^2+rR+R^2)h/3";
        geoEq[13] = "V=4/3*πr^3";
        geoEq[14] = "V=lwh/3";
        equationStorage[1] = geoEq;

        ArrayList<String> stat = new ArrayList<>();
        stat.add("Binomial Distribution Mean X");
        stat.add("Binomial Distribution Mean Y");
        stat.add("Pooled Standard Deviation");
        stat.add("Regression Line at Mean");
        stat.add("Simple Linear Regression");
        stat.add("Slope of Linear Regression");
        stat.add("Z-Score");
        String[] statEq = new String[stat.size()];
        statEq[0] = "[σx]=sqrt(np(1-p))";
        statEq[1] = "[σp]=sqrt(p(1-p)/n)";
        statEq[2] = "[sp]=sqrt((([n₁]-1)[s₁]^2+([n₂]-1)[s₂]^2)/([n₁]+[n₂]-2))";
        statEq[3] = "[b₀]=[y¯]-[b₁][x¯]";
        statEq[4] = "[y^]=[b₀]+[b₁]x";
        statEq[5] = "[b₁]=r[sy]/[sx]";
        statEq[6] = "z=(x-[µ])/[σ]";
        equationStorage[2] = statEq;

        ArrayList<String> chem = new ArrayList<>();
        chem.add("0th-Order Integrated Rate Law");
        chem.add("1st-Order Integrated Rate Law");
        chem.add("2nd-Order Integrated Rate Law");
        chem.add("Arrhenius Equation");
        chem.add("Celsius-Kelvin");
        chem.add("Coulomb's Law");
        chem.add("Energy of a Photon");
        chem.add("Gibbs Free Energy");
        chem.add("Half-Life Equation");
        chem.add("Ideal Gas Law");
        chem.add("Kc-Kp");
        chem.add("Law of Mass Action");
        chem.add("Nernst Equation");
        chem.add("Specific Heat Formula");

        String[] chemEq = new String[chem.size()];
        chemEq[0] = "[At]=-kt+[A₀]";
        chemEq[1] = "ln[At]=-kt+ln[A₀]";
        chemEq[2] = "1/[At]=kt+1/[A₀]";
        chemEq[3] = "k=A*e^(-[Ea]/(RT))";
        chemEq[4] = "K=C+273";
        chemEq[5] = "F=k[q₁][q₂]/r^2";
        chemEq[6] = "E=hc/[λ]";
        chemEq[7] = "[∆G]=[∆H]-T[∆S]";
        chemEq[8] = "M=[M₀]*e^(-kt/ln0.5)";
        chemEq[9] = "PV=nRT";
        chemEq[10] = "[Kp]=[Kc](RT)^[∆n]";
        chemEq[11] = "K=([C]^c[D]^d)/([A]^a[B]^b)";
        chemEq[12] = "E=[E₀]-KT/(nF)lnQ";
        chemEq[13] = "Q=mC[∆T]";
        equationStorage[3] = chemEq;

        ArrayList<String> phys = new ArrayList<>();
        phys.add("Acceleration Equation");
        phys.add("Centripetal Acceleration");
        phys.add("Impulse-Momentum");
        phys.add("Kinematic Equation");
        phys.add("Kinetic Energy");
        phys.add("Magnetic Force Formula");
        phys.add("Simple Pendulum");
        phys.add("Time Dilation");
        phys.add("Universal Gravitation");
        String[] physEq = new String[phys.size()];
        physEq[0] = "s=[s₀]+[v₀]t+1/2at^2";
        physEq[1] = "a=v^2/r";
        physEq[2] = "F[∆t]=m[∆v]";
        physEq[3] = "v^2=[v₀]^2+2a(x-[x₀])";
        physEq[4] = "K=1/2mv^2";
        physEq[5] = "F=qvbsin[Θ]";
        physEq[6] = "T=2*πsqrt(l/g)";
        physEq[7] = "[t']=t/sqrt(1-v^2/c^2)";
        physEq[8] = "[Fg]=G[m₁][m₂]/r^2";
        equationStorage[4] = physEq;
        equationStorage[5] = custEq;

        listHashMap.put(listHeader.get(0), alg);
        listHashMap.put(listHeader.get(1), geo);
        listHashMap.put(listHeader.get(2), stat);
        listHashMap.put(listHeader.get(3), chem);
        listHashMap.put(listHeader.get(4), phys);
        listHashMap.put(listHeader.get(5), cust);

    }

    public static void addCustom(String text, String equation, int position) {
        cust.add(position, text);
        String[] custEqTemp = new String[custEq.length + 1];
        int count = 0;
        for (int i = 0; i < custEqTemp.length; i++) {
            if (i != position) {
                custEqTemp[i] = custEq[count];
                count++;
            }
        }
        custEqTemp[position] = equation;
        custEq = custEqTemp;
    }

    public static void deleteCustom(int position) {
        cust.remove(position);
        String[] custEqTemp = new String[custEq.length - 1];
        int count = 0;
        for (int i = 0; i < custEq.length; i++) {
            if (i != position) {
                custEqTemp[count] = custEq[i];
                count++;
            }
        }
        custEq = custEqTemp;
    }

    public static ArrayList<String> getCustom() {
        return cust;
    }

    public static String[][] getEqStorage() {
        return equationStorage;
    }

    public static void updateEqStorage() {
        equationStorage[5] = custEq;
    }

    public static String[] getCustomEq() {
        return custEq;
    }

    public Object readData(String filename) {
        Object returnObject = "";
        try {
            FileInputStream fis = openFileInput(filename);
            ObjectInputStream ois = new ObjectInputStream(fis);
            returnObject = ois.readObject();
            String temp = returnObject.toString();
            ois.close();
            fis.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnObject;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void saveData(String filename, Object o) {
        try {
            FileOutputStream fos = openFileOutput(filename, MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(o);
            String temp = o.toString();
            oos.flush();
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
