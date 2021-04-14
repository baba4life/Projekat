package com.example.nilme.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.nilme.Model.User
import com.example.nilme.R
import com.example.nilme.fragments.ProfileFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import org.jetbrains.annotations.NotNull
import org.w3c.dom.DocumentFragment

class UserAdapter(private var mContext: Context,
                  private var mUser: List<User>,
                  private var isFragment: Boolean=false): RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.user_item_layout, parent, false)
        return UserAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUser.size
    }

    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int) {
        val user = mUser[position]
        holder.usernameTextView.text = user.getUsername()
        holder.userFullnameTextView.text = user.getFullname()
        Picasso.get().load(user.getImage()).placeholder(R.drawable.profile_icon).into(holder.userProfileImage)

        checkFollowingStatus(user.getUID(),holder.followButton)

        holder.itemView.setOnClickListener{
            val pref = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
            pref.putString("profileId", user.getUID())
            pref.apply()

            (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container , ProfileFragment()).commit()
        }

        //za follow ljudi i da pise following ako vec pratimo usera
        holder.followButton.setOnClickListener {
            if (holder.followButton.text.toString() == "Follow") {
                firebaseUser?.uid.let { it1 ->
                    //referenc to root
                    FirebaseDatabase.getInstance().reference
                            //uzima uid od usera
                            .child("Follow").child(it1.toString())
                            // doda se u following list odgovarajuci user
                            .child("Following").child(user.getUID())
                            .setValue(true).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    firebaseUser?.uid.let { it1 ->
                                        //referenc to root
                                        FirebaseDatabase.getInstance().reference
                                                //uzima uid od usera
                                                .child("Follow").child(user.getUID())
                                                // doda se u following list odgovarajuci user
                                                .child("Followers").child(it1.toString())
                                                .setValue(true).addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {

                                                    }
                                                }
                                    }
                                }
                            }
                }
            } else {
                firebaseUser?.uid.let { it1 ->
                    //referenc to root
                    FirebaseDatabase.getInstance().reference
                            //uzima uid od usera
                            .child("Follow").child(it1.toString())
                            // doda se u following list odgovarajuci user
                            .child("Following").child(user.getUID())
                            .removeValue().addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    firebaseUser?.uid.let { it1 ->
                                        //referenc to root
                                        FirebaseDatabase.getInstance().reference
                                                //uzima uid od usera
                                                .child("Follow").child(user.getUID())
                                                // doda se u following list odgovarajuci user
                                                .child("Followers").child(it1.toString())
                                                .removeValue().addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {

                                                    }
                                                }
                                    }
                                }
                            }

                }
            }
        }
    }


    class ViewHolder(@NotNull itemView: View) : RecyclerView.ViewHolder(itemView) {
            var usernameTextView: TextView = itemView.findViewById(R.id.user_name_search)
            var userFullnameTextView: TextView = itemView.findViewById(R.id.user_full_name_search)
            var userProfileImage: CircleImageView = itemView.findViewById(R.id.user_profile_search)
            var followButton: Button = itemView.findViewById(R.id.follow_btn_search)


        }
    private fun checkFollowingStatus(uid: String, followButton: Button)
    {
       val followingRef = firebaseUser?.uid.let { it1 ->
           //referenc to root
           FirebaseDatabase.getInstance().reference
                   //uzima uid od usera
                   .child("Follow").child(it1.toString())
                   // doda se u following list odgovarajuci user
                   .child("Following")
       }
        followingRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(datasnapshot: DataSnapshot) {
                if(datasnapshot.child(uid).exists()){
                    followButton.text = "Following"
                }else{
                    followButton.text = "Follow"
                }
            }

        })
    }
}




