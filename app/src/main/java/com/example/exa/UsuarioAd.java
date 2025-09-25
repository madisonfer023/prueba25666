package com.example.exa;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// El nombre de la clase es "UsuarioAd"
public class UsuarioAd extends RecyclerView.Adapter<UsuarioAd.ViewHolderItem> {

    // --- Variables de la clase ---
    private final JSONArray jsonDataSource;
    private final Context parentContext;

    /**
     * Constructor del adaptador.
     * @param dataSource El JSONArray con los datos de los usuarios.
     * @param context El contexto de la actividad que está usando este adaptador.
     */
    // CORRECCIÓN AQUÍ: El nombre del constructor ahora es "UsuarioAd" para coincidir con la clase.
    public UsuarioAd(JSONArray dataSource, Context context) {
        this.jsonDataSource = dataSource;
        this.parentContext = context;
    }

    @NonNull
    @Override
    public ViewHolderItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parentContext).inflate(R.layout.item_usuario, parent, false);
        return new ViewHolderItem(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderItem holder, int position) {
        try {
            JSONObject currentUserObject = jsonDataSource.getJSONObject(position);

            JSONObject nameObject = currentUserObject.getJSONObject("name");
            String nameFirst = nameObject.getString("first");
            String nameLast = nameObject.getString("last");
            String completeName = nameFirst + " " + nameLast;

            String userEmail = currentUserObject.getString("email");
            String userCountry = currentUserObject.getJSONObject("location").getString("country");
            String avatarUrl = currentUserObject.getJSONObject("picture").getString("thumbnail");

            holder.userNameTextView.setText(completeName);
            holder.userEmailTextView.setText(userEmail);
            holder.userCountryTextView.setText(userCountry);

            Glide.with(parentContext)
                    .load(avatarUrl)
                    .circleCrop()
                    .into(holder.userAvatar);

            holder.itemView.setOnClickListener(view -> {
                // Asegúrate de que tu clase de detalle se llame 'Detalle'
                Intent navigationIntent = new Intent(parentContext, Detalle.class);
                navigationIntent.putExtra("userData", currentUserObject.toString());
                parentContext.startActivity(navigationIntent);
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return jsonDataSource.length();
    }

    public static class ViewHolderItem extends RecyclerView.ViewHolder {
        ImageView userAvatar;
        TextView userNameTextView, userEmailTextView, userCountryTextView;

        public ViewHolderItem(@NonNull View itemView) {
            super(itemView);
            userAvatar = itemView.findViewById(R.id.imgUserProfile);
            userNameTextView = itemView.findViewById(R.id.txtUserName);
            userEmailTextView = itemView.findViewById(R.id.txtUserEmail);
            userCountryTextView = itemView.findViewById(R.id.txtUserCountry);
        }
    }
}