package com.techomite.math.pluggr;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Steven Yu on 4/4/2018.
 * Modified by Steven Yu on 6/6/2019
 */

public class CalculatorActivity extends Fragment {
    public String[] inputVars;
    private Adapter varsAdapter;
    private Parser parser;
    private Tree eqTree;
    private ArrayList<String> eqHistory;
    private ArrayList<String> inputHistory;
    private ArrayList<String> rootHistory;
    public static boolean radians;

    private Button convBtn1;
    private Button convBtn2;
    private Button convBtn3;
    private Button convBtn4;
    private Button convBtn5;
    private View rootView;
    private ListView varsList;
    private EditText input;
    private Button eqTriangle;
    private Button xBtn;
    private Button solveBtn;
    private Button confirmBtn;
    private MathJaxWebView mathJaxWebView;
    private ImageButton back;
    private ProgressBar progressBar;

    private final int MODE_EQUATION = 0;
    private final int MODE_VARS = 1;
    private int mode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Load Data
        ArrayList<ArrayList<String>> history_default = new ArrayList<>();
        history_default.add(new ArrayList<String>());
        history_default.add(new ArrayList<String>());
        history_default.add(new ArrayList<String>());
        ArrayList<ArrayList<String>> history = (ArrayList<ArrayList<String>>) readData("history_storage", history_default);
        eqHistory = history.get(0);
        inputHistory = history.get(1);
        rootHistory = history.get(2);
        radians = (boolean) readData("radians_setting", false);

        //Initializing Views
        rootView = inflater.inflate(R.layout.calculator, container, false);
        varsList = (ListView) rootView.findViewById(R.id.varsListView);
        input = (EditText) rootView.findViewById(R.id.eqText);
        eqTriangle = (Button) rootView.findViewById(R.id.eqListImageButton);
        xBtn = (Button) rootView.findViewById(R.id.delete);
        solveBtn = (Button) rootView.findViewById(R.id.solve);
        confirmBtn = (Button) rootView.findViewById(R.id.confirm);
        back = (ImageButton) rootView.findViewById(R.id.edit);
        mathJaxWebView = (MathJaxWebView) rootView.findViewById(R.id.webView);
        mathJaxWebView.getSettings().setJavaScriptEnabled(true);
        convBtn1 = (Button) rootView.findViewById(R.id.convBtn1);
        convBtn2 = (Button) rootView.findViewById(R.id.convBtn2);
        convBtn3 = (Button) rootView.findViewById(R.id.convBtn3);
        convBtn4 = (Button) rootView.findViewById(R.id.convBtn4);
        convBtn5 = (Button) rootView.findViewById(R.id.convBtn5);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        mode = (int) readData("input_mode", MODE_EQUATION);
        changeMode(mode);

        if (input.getText().length() > 0) {
            eqTriangle.setVisibility(View.GONE);
        } else {
            xBtn.setVisibility(View.GONE);
        }
        eqTriangle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SelectActivity.class);
                startActivity(intent);
            }
        });

        input.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                String equation = s.toString();
                equation = equation.replaceAll("\\s", ""); //clears whitespace
                if (equation.equals("")) {
                    xBtn.setVisibility(View.GONE);
                    eqTriangle.setVisibility(View.VISIBLE);
                } else {
                    xBtn.setVisibility(View.VISIBLE);
                    eqTriangle.setVisibility(View.GONE);
                }
                equation = equation.replace("√", "sqrt");
                equation = equation.replace("×", "*");
                equation = equation.replace("•", "*");
                equation = equation.replace("÷", "/");
                saveData("equation_storage", input.getText().toString());

                EquationHandler handler = new EquationHandler(equation);
                parser = new Parser(handler);

                if (!parser.isValid()) {
                    mathJaxWebView.setText("Invalid Equation");
                    confirmBtn.setEnabled(false);
                    confirmBtn.setBackgroundColor(Color.parseColor("#d5d5d5"));
                } else {
                    confirmBtn.setEnabled(true);
                    confirmBtn.setBackgroundColor(Color.parseColor("#4285f4"));
                    if (parser.isComplex()) {
                        Tree right = parser.getData().getBranches().get(0);
                        Tree left = parser.getData().getBranches().get(1);
                        mathJaxWebView.setText("\\[" + left.toTex() + "=" + right.toTex() + "\\]");
                    } else {
                        mathJaxWebView.setText("\\[" + parser.getData().toTex() + "\\]");
                    }
                }

                eqTree = parser.getData();
                ArrayList<VariableLabel> variables = eqTree.getVariables();
                inputVars = new String[variables.size()];
                for (int i = 0; i < variables.size(); i++) {
                    inputVars[i] = variables.get(i).getVariable();
                }
                boolean scientific = (boolean) readData("scientific_switch", false);
                if (!scientific) {
                    varsAdapter = new Adapter(getContext(), R.layout.vars_listview_detail, inputVars);
                } else {
                    varsAdapter = new Adapter(getContext(), R.layout.vars_listview_detail_exp, inputVars);
                }
                varsList.setAdapter(varsAdapter);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        //Check if came from equation selection
        Intent intent = getActivity().getIntent();
        String text = intent.getStringExtra("com.example.stevenyu.pluggr.SET_EQUATION");
        if (text != null) {
            input.setText(text);
            input.setSelection(text.length());
        }
        String equation = readData("equation_storage", "").toString();
        input.setText(equation);
        input.setSelection(equation.length());

        xBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input.setText("");
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode = MODE_VARS;
                changeMode(mode);
                saveData("input_mode", mode);
            }
        });

        solveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (varsAdapter.numEmpty() <= (parser.isComplex() ? 1 : 0)) {
                    solveBtn.setEnabled(false);
                    solveBtn.setBackgroundColor(Color.parseColor("#d5d5d5"));

                    HashMap<String, Double> variableMap = new HashMap<>();
                    for (int i = 0; i < varsAdapter.size(); i++) {
                        Double value = varsAdapter.getInput(i);
                        if (value != null) {
                            variableMap.put(varsAdapter.getVariable(i), value);
                        }
                    }
                    eqTree.plugVariables(variableMap);
                    eqTree.simplify();

                    String rootDisplay = "";
                    if (!parser.isComplex()) {
                        progressBar.setVisibility(View.VISIBLE);
                        if (eqTree.getLabel() instanceof NumberLabel) {
                            NumberLabel numberLabel = (NumberLabel) eqTree.getLabel();
                            rootDisplay += cleanDisplay(numberLabel.getValue());
                        }
                    } else {
                        if (varsAdapter.numEmpty() == 0) {
                            if (eqTree.getLabel() instanceof NumberLabel) {
                                NumberLabel self = (NumberLabel) eqTree.getLabel();
                                if (self.getValue() == 0) {
                                    rootDisplay = "TRUE";
                                } else {
                                    rootDisplay = "FALSE";
                                }
                            }
                        } else { //(varsAdapter.numEmpty() == 1)
                            ArrayList<Double> roots;
                            String unknown = eqTree.getVariables().get(0).getVariable();
                            NewtonRaphson nr = new NewtonRaphson(eqTree);
                            roots = nr.getRoots();
                            Collections.sort(roots);
                            String previous = ""; //remove dupes
                            if (roots.size() > 0) {
                                rootDisplay = unknown + "  =  ";
                                for (int k = 0; k < roots.size(); k++) {
                                    String cleaned = cleanDisplay(roots.get(k));
                                    if (cleaned.equals("" + roots.get(k))) { //no change
                                        String clean_rounded = cleanDisplay(Util.round(roots.get(k), 15));
                                        if (!clean_rounded.equals(previous)) { //add only if not dupe
                                            rootDisplay += clean_rounded + ",  ";
                                        }
                                        previous = clean_rounded;
                                    } else {
                                        if (!cleaned.equals(previous)) { //add only if not dupe
                                            rootDisplay += cleaned + ",  ";
                                        }
                                        previous = cleaned;
                                    }
                                }
                                rootDisplay = rootDisplay.substring(0, rootDisplay.length() - 3); //remove last comma
                            } else {
                                rootDisplay += "NO SOLUTIONS FOUND";
                            }
                        }
                    }
                    if (eqHistory.size() > 24) {
                        eqHistory.remove(24);
                        inputHistory.remove(24);
                        rootHistory.remove(24);
                    }
                    eqHistory.add(0, input.getText().toString());
                    rootHistory.add(0, rootDisplay);
                    String string = "";
                    for (int x = 0; x < inputVars.length; x++) {
                        string += inputVars[x] + "  =  ";
                        if (varsAdapter.getInput(x) == null || varsAdapter.getInput(x).equals("")) {
                            string += "?";
                        } else {
                            double num = varsAdapter.getInput(x);
                            string += num;
                        }
                        if (x < inputVars.length - 1) {
                            string += ",  ";
                        }
                    }
                    inputHistory.add(0, string);

                    ArrayList<ArrayList<String>> temp = new ArrayList<>(3);
                    temp.add(eqHistory);
                    temp.add(inputHistory);
                    temp.add(rootHistory);
                    saveData("history_storage", temp);

                    progressBar.setVisibility(View.GONE);
                    Intent intentResult = new Intent(getActivity(), ResultActivity.class);
                    intentResult.putExtra("result_storage", new String[]{input.getText().toString(), string, rootDisplay});
                    startActivity(intentResult);
                    solveBtn.setEnabled(true);
                    solveBtn.setBackgroundColor(Color.parseColor("#4285f4"));
                } else {
                    Toast.makeText(getContext(), "One or more variables are not defined.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode = MODE_EQUATION;
                changeMode(mode);
                saveData("input_mode", mode);
            }
        });

        String[] buttons_default = new String[]{"[", "]", "=", "^", "π"};
        String[] buttons = (String[]) readData("button_chars_storage", buttons_default);

        convBtn1.setText(buttons[0]);
        convBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int cursorPosition = input.getSelectionStart();
                String text = input.getText().toString();
                input.setText(text.substring(0, cursorPosition) + convBtn1.getText().toString() + text.substring(cursorPosition, text.length()));
                input.setSelection(cursorPosition + 1);
            }
        });
        convBtn2.setText(buttons[1]);
        convBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int cursorPosition = input.getSelectionStart();
                String text = input.getText().toString();
                input.setText(text.substring(0, cursorPosition) + convBtn2.getText().toString() + text.substring(cursorPosition, text.length()));
                input.setSelection(cursorPosition + 1);
            }
        });
        convBtn3.setText(buttons[2]);
        convBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int cursorPosition = input.getSelectionStart();
                String text = input.getText().toString();
                input.setText(text.substring(0, cursorPosition) + convBtn3.getText().toString() + text.substring(cursorPosition, text.length()));
                input.setSelection(cursorPosition + 1);
            }
        });
        convBtn4.setText(buttons[3]);
        convBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int cursorPosition = input.getSelectionStart();
                String text = input.getText().toString();
                input.setText(text.substring(0, cursorPosition) + convBtn4.getText().toString() + text.substring(cursorPosition, text.length()));
                input.setSelection(cursorPosition + 1);
            }
        });
        convBtn5.setText(buttons[4]);
        convBtn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int cursorPosition = input.getSelectionStart();
                String text = input.getText().toString();
                input.setText(text.substring(0, cursorPosition) + convBtn5.getText().toString() + text.substring(cursorPosition, text.length()));
                input.setSelection(cursorPosition + 1);
            }
        });
        return rootView;
    }

    /**
     * Returns a cleaned version of root
     */
    private String cleanDisplay(double root) {
        //Double to Int cleaning
        long rounded = Math.round(root);
        if (rounded == root) {
            return "" + rounded;
        }

        String symbol = "";
        long factor = 0;
        //Pi and e cleaning
        double pi_clean = root / Math.PI;
        rounded = Math.round(pi_clean);
        if (Math.abs(rounded - pi_clean) <= Math.pow(10, Util.power(pi_clean) - 15)) {
            factor = rounded;
            symbol = "π";
        } else {
            double e_clean = root / Math.E;
            rounded = Math.round(e_clean);
            if (Math.abs(rounded - e_clean) <= Math.pow(10, Util.power(pi_clean) - 15)) {
                factor = rounded;
                symbol = "e";
            }
        }

        if (!symbol.equals("")) {
            if (factor == 1) {
                return symbol;
            } else if (factor == -1) {
                return "-" + symbol;
            } else if (factor != 0) {
                return factor + symbol;
            }
        }
        return root + "";
    }

    private void changeMode(int mode) {
        if (mode == MODE_VARS) {
            confirmBtn.setVisibility(View.GONE);
            varsList.setVisibility(View.VISIBLE);
            solveBtn.setVisibility(View.VISIBLE);
            back.setVisibility(View.VISIBLE);
            input.setVisibility(View.GONE);
            convBtn1.setVisibility(View.GONE);
            convBtn2.setVisibility(View.GONE);
            convBtn3.setVisibility(View.GONE);
            convBtn4.setVisibility(View.GONE);
            convBtn5.setVisibility(View.GONE);

            ConstraintSet constraintSet = new ConstraintSet();
            ConstraintLayout constraintLayout = (ConstraintLayout) rootView.findViewById(R.id.root_layout);
            constraintSet.clone(constraintLayout);
            constraintSet.connect(R.id.webView, ConstraintSet.TOP, R.id.root_layout, ConstraintSet.TOP);
            constraintSet.applyTo(constraintLayout);

        } else if (mode == MODE_EQUATION) {
            confirmBtn.setVisibility(View.VISIBLE);
            varsList.setVisibility(View.GONE);
            solveBtn.setVisibility(View.GONE);
            back.setVisibility(View.GONE);
            input.setVisibility(View.VISIBLE);
            convBtn1.setVisibility(View.VISIBLE);
            convBtn2.setVisibility(View.VISIBLE);
            convBtn3.setVisibility(View.VISIBLE);
            convBtn4.setVisibility(View.VISIBLE);
            convBtn5.setVisibility(View.VISIBLE);

            ConstraintSet constraintSet = new ConstraintSet();
            ConstraintLayout constraintLayout = (ConstraintLayout) rootView.findViewById(R.id.root_layout);
            constraintSet.clone(constraintLayout);
            constraintSet.connect(R.id.webView, ConstraintSet.TOP, R.id.btnListGuideLine, ConstraintSet.BOTTOM);
            constraintSet.applyTo(constraintLayout);
        }
    }

    private void saveData(String filename, Object o) {
        try {
            FileOutputStream fos = getContext().openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(o);
            oos.flush();
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Object readData(String filename, Object _default) {
        try {
            FileInputStream fis = getContext().openFileInput(filename);
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object returnObject = ois.readObject();
            ois.close();
            fis.close();
            return returnObject;
        } catch (ClassNotFoundException | IOException e) {
            return _default;
        }
    }
}
