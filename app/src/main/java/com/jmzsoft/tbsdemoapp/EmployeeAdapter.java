package com.jmzsoft.tbsdemoapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.ViewHolder> implements Filterable {

    private Context context;
    private ArrayList<Employees> employees;
    private ArrayList<Employees> employeeListFiltered;
    private int selectedPos = RecyclerView.NO_POSITION;

    EmployeeAdapter(Context context, ArrayList<Employees> employees) {
        this.context = context;
        this.employees = employees;
        this.employeeListFiltered = employees;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvId;
        TextView tvName;
        TextView tvAge;
        TextView tvSalary;

        ViewHolder(@NonNull View view) {
            super(view);
            this.tvId = view.findViewById(R.id.tvId);
            this.tvName = view.findViewById(R.id.tvName);
            this.tvAge = view.findViewById(R.id.tvAge);
            this.tvSalary = view.findViewById(R.id.tvSalary);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.row_item, parent, false)
        );
    }

    @SuppressLint("StringFormatInvalid")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        if(selectedPos==position) {
            holder.itemView.setBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));

        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#FEFEFE"));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPos = position;
                notifyDataSetChanged();
            }
        });

        final Employees employee = employeeListFiltered.get(position);
        Resources res = context.getResources();
        holder.tvId.setText(res.getString(R.string.id, employee.getId()));
        holder.tvName.setText(res.getString(R.string.name, employee.getEmployeeName()));
        holder.tvAge.setText(res.getString(R.string.age, employee.getEmployeeAge()));
        holder.tvSalary.setText(res.getString(R.string.salary, employee.getEmployeeSalary()));
    }

    @Override
    public int getItemCount() {
        return this.employeeListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    employeeListFiltered = employees;
                } else {
                    ArrayList<Employees> filteredList = new ArrayList<>();
                    for (Employees row : employees) {
                        if (row.getEmployeeName().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    employeeListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = employeeListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                employeeListFiltered = (ArrayList<Employees>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}