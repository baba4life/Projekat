package com.example.nilme.fragments


import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.nilme.AccountSettingActivity
import com.example.nilme.Model.User
import com.example.nilme.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var profileId : String
    private lateinit var firebaseUser : FirebaseUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        firebaseUser = FirebaseAuth.getInstance().currentUser!! // uzvicnici su tu da se zna da ne sme da bude null
        val pref = context?.getSharedPreferences("PREFS",Context.MODE_PRIVATE)

        if(pref != null){
               this.profileId = pref.getString("profileId","none").toString()

        }


        // tekuci user
        if(profileId == firebaseUser.uid)
        {
            view.edit_profile.text = "Edit Profile"
        }else if(profileId != firebaseUser.uid)
        {
            checkFollowAndFollowingButtnoStatus()
        }


        view.edit_profile.setOnClickListener {
            val getButtonText = view.edit_profile.text.toString()

            when
            {
                getButtonText == "Edit Profile" -> startActivity(Intent(context,AccountSettingActivity::class.java))

                getButtonText == "Follow" ->{
                    firebaseUser?.uid.let { it1 ->
                        //referenc to root
                        FirebaseDatabase.getInstance().reference
                                // doda se u follow list odgovarajuci user
                                .child("Follow").child(it1.toString())
                                //uzima uid od usera i stavlja u listu following
                                .child("Following").child(profileId)
                                .setValue(true)
                    }
                    firebaseUser?.uid.let { it1 ->
                        //referenc to root
                        FirebaseDatabase.getInstance().reference
                                //uzima uid od usera
                                .child("Follow").child(profileId)
                                // doda se u following list odgovarajuci user
                                .child("Followers").child(it1.toString())
                                .setValue(true)
                    }
                }

                getButtonText == "Following" ->{
                    firebaseUser?.uid.let { it1 ->
                        //referenc to root
                        FirebaseDatabase.getInstance().reference
                                // doda se u follow list odgovarajuci user
                                .child("Follow").child(it1.toString())
                                //uzima uid od usera i stavlja u listu following
                                .child("Following").child(profileId)
                                .removeValue()
                    }
                    firebaseUser?.uid.let { it1 ->
                        //referenc to root
                        FirebaseDatabase.getInstance().reference
                                //uzima uid od usera
                                .child("Follow").child(profileId)
                                // doda se u following list odgovarajuci user
                                .child("Followers").child(it1.toString())
                                .removeValue()
                    }
                }
            }

        }

        getFollowers()
        getFollowing()
        userInfo()

        return view
    }
    // provera koji useri se prate
    private fun checkFollowAndFollowingButtnoStatus() {
        val followingRef = firebaseUser?.uid.let { it1 ->
            //referenc to root
            FirebaseDatabase.getInstance().reference
                    //uzima uid od usera
                    .child("Follow").child(it1.toString())
                    // doda se u following list odgovarajuci user
                    .child("Following")
        }

        if(followingRef != null){
            followingRef.addValueEventListener(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    if(p0.child(profileId).exists()){
                        view?.edit_profile?.text = "Following"
                    }else
                    {
                        view?.edit_profile?.text = "Follow"
                    }
                }

            })
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun getFollowers(){
        //referenc to root
        val followersRef =  FirebaseDatabase.getInstance().reference
                    //uzima uid od usera
                    .child("Follow").child(profileId)
                    // doda se u following list odgovarajuci user
                    .child("Followers")


        followersRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists())
                {
                    view?.total_followers?.text = p0.childrenCount.toString()
                }
            }

        })
    }

    private fun getFollowing(){
        //referenc to root
        val followersRef = FirebaseDatabase.getInstance().reference
                    //uzima uid od usera
                    .child("Follow").child(profileId)
                    // doda se u following list odgovarajuci user
                    .child("Following")


        followersRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists())
                {
                    view?.total_following?.text = p0.childrenCount.toString()
                }
            }

        })
    }

    private fun userInfo(){
        val userRef = FirebaseDatabase.getInstance().getReference()
                .child("Users")
                .child(profileId)

        userRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    val user = snapshot.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile_icon).into(view?.profile_image)
                    view?.profile_fragment_username?.text = user.getUsername()
                    view?.full_name_flag?.text = user.getFullname()
                    view?.bio_frofile_flag?.text = user.getBio()


                }
            }

        })
    }

    override  fun onStop()
    {
        super.onStop()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onPause()
    {
        super.onPause()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }
}