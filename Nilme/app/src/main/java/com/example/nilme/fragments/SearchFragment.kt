package com.example.nilme.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nilme.Adapter.UserAdapter
import com.example.nilme.Model.User
import com.example.nilme.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_search.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment() {

    private var recycleView: RecyclerView? = null
    private var userAdapter: UserAdapter? =null
    private var mUser : MutableList<User>? =null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_search, container, false)

        recycleView = view.findViewById(R.id.recycler_view_search)
        recycleView?.setHasFixedSize(true)
        recycleView?.layoutManager = LinearLayoutManager(context)

        mUser = ArrayList()
        userAdapter = context?.let { UserAdapter(it,mUser as ArrayList<User>,true) }
        recycleView?.adapter = userAdapter
        // da search radi na kucanje reci u edit textu
        view.search_edit_text.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(view.search_edit_text.text.toString() == null){

                }else{
                    recycleView?.visibility = View.VISIBLE

                    retreveUsers()
                    //da nije bitno da li je veliko ili malo slovo
                    searchUser(s.toString().toLowerCase())
                }
            }

        })

        return view
    }

    private fun searchUser(input: String) {
        val query = FirebaseDatabase.getInstance().getReference()
            .child("Users")
            .orderByChild("fullname")
            .startAt(input)
            .endAt(input + "\uf8ff") // bitno da radi search

        query.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(datasnapshot: DataSnapshot) {
                mUser?.clear()
                //radice dok sve usere ne storuje u nasu listu
                for(snapshot in datasnapshot.children)
                {
                    //brzo citanje usera u searchbaru
                    val user = snapshot.getValue(User::class.java)

                    if(user != null){
                        //ucitava iz baze usera
                        mUser?.add(user)
                    }
                }
                userAdapter?.notifyDataSetChanged()
            }
        } )

    }

    private fun retreveUsers() {
        val userRef = FirebaseDatabase.getInstance().getReference().child("Users")
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(datasnapshot: DataSnapshot) {
                if(view?.search_edit_text?.text.toString() == null){
                    mUser?.clear()
                    //radice dok sve usere ne storuje u nasu listu
                    for(snapshot in datasnapshot.children)
                    {
                        //brzo citanje usera u searchbaru
                        val user = snapshot.getValue(User::class.java)

                        if(user != null){
                            //ucitava iz baze usera
                            mUser?.add(user)
                        }
                    }
                    userAdapter?.notifyDataSetChanged()
                }
            }
        } )


    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}