package edu.upenn.cis573.project;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MakeDonationActivity extends AppCompatActivity {

    private Organization selectedOrg;
    private Fund selectedFund;
    private DataManager dataManager = new DataManager(new WebClient("10.0.2.2", 3001));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_donation);

        final List<Organization> orgs = dataManager.getAllOrganizations();
        selectedOrg = orgs.get(0);
        if (selectedOrg.getFunds().isEmpty() == false) {
            selectedFund = selectedOrg.getFunds().get(0);
        }
        else {
            selectedFund = new Fund("0", "This Organization has no Funds.", 0, 0);
        }

        final Spinner orgSpinner = findViewById(R.id.orgSpinner);
        final Spinner fundSpinner = findViewById(R.id.fundSpinner);

        orgSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String selectedOrgName = (String) adapterView.getItemAtPosition(i);

                for (Organization org : orgs) {

                    if (org.getName().equals(selectedOrgName)) {
                        selectedOrg = org;
                        break;
                    }

                }

                Log.v("spinner", "Selected org: " + selectedOrg.getName() + "; num funds = " + selectedOrg.getFunds().size());

                List<String> fundNames = new LinkedList<>();
                if (selectedOrg.getFunds().isEmpty() == false) {
                    for (Fund fund : selectedOrg.getFunds()) {
                        fundNames.add(fund.getName());
                    }
                }
                else {
                    fundNames.add("This Organization has no Funds.");
                }

                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, fundNames);

                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                fundSpinner.setAdapter(dataAdapter);


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        fundSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedFundName = (String)adapterView.getItemAtPosition(i);

                if (selectedOrg.getFunds().isEmpty() == false) {
                    for (Fund f : selectedOrg.getFunds()) {
                        if (f.getName().equals(selectedFundName)) {
                            selectedFund = f;
                            break;
                        }
                    }
                }
                else {
                    selectedFund = new Fund("0", "This Organization has no Funds.", 0, 0);
                }

                Log.v("spinner", "Selected fund: " + selectedFundName);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        List<String> orgNames = new LinkedList<>();
        for (Organization org : orgs) {
            orgNames.add(org.getName());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, orgNames);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        orgSpinner.setAdapter(dataAdapter);

    }


    public void onMakeDonationButtonClick(View view) {

        String orgId = selectedOrg.getId();
        String fundId = selectedFund.getId();

        if (fundId.equals("0")) {
            Toast.makeText(this, "Sorry, this Organization does not have any Funds.", Toast.LENGTH_LONG).show();
            return;
        }

        EditText amountField = findViewById(R.id.amountField);
        String amountString = amountField.getText().toString();
        long amount;

        try {
            amount = Long.parseLong(amountString);
            if (amount <= 0) {
                Toast.makeText(this, "Invalid donation amount. Please enter a positive value.", Toast.LENGTH_LONG).show();
                amountField.setText(""); // clears the text field for re-entry
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid donation amount. Please enter a valid number.", Toast.LENGTH_LONG).show();
            amountField.setText(""); // clears the text field for re-entry
            return;
        }

        Contributor contributor = MainActivity.contributor;
        String contributorId = contributor.getId();

        Log.v("makeDonation", orgId + " " + fundId + " " + amount + " " + contributorId);

        boolean success = dataManager.makeDonation(contributorId, fundId, String.valueOf(amount));

        if (success) {
            Toast.makeText(this, "Thank you for your donation!", Toast.LENGTH_LONG).show();
            contributor.getDonations().add(new Donation(selectedFund.getName(), contributor.getName(), amount, new Date().toString()));

            Executor executor = Executors.newSingleThreadExecutor();
            executor.execute( () -> {
                try { Thread.sleep(3000); } catch (Exception e) { }
                finish();
            });
        }
        else {
            Toast.makeText(this, "Sorry, something went wrong!", Toast.LENGTH_LONG).show();
        }
    }

}