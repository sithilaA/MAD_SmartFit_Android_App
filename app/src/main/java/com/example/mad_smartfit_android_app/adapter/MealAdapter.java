package com.example.mad_smartfit_android_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mad_smartfit_android_app.R;
import com.example.mad_smartfit_android_app.model.Meal;

import java.util.List;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {
    private List<Meal> mealList;

    public static class MealViewHolder extends RecyclerView.ViewHolder {
        public TextView mealName, ingredients, notes;


        public MealViewHolder(View itemView) {
            super(itemView);
            mealName = itemView.findViewById(R.id.mealName);
            ingredients = itemView.findViewById(R.id.ingredients);
            notes = itemView.findViewById(R.id.notes);

        }
    }

    public MealAdapter(List<Meal> mealList) {
        this.mealList = mealList;
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.meal_card, parent, false);
        return new MealViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        Meal meal = mealList.get(position);
        holder.mealName.setText(meal.getMealName());
        holder.ingredients.setText(meal.getIngredients());
        holder.notes.setText(meal.getNotes());
        // Load image using Glide or Picasso
        // Glide.with(holder.itemView.getContext()).load(meal.getImageUrl()).into(holder.mealImage);
    }

    @Override
    public int getItemCount() {
        return mealList.size();
    }

    public void setMeals(List<Meal> meals) {
        this.mealList = meals;
        notifyDataSetChanged();
    }
}