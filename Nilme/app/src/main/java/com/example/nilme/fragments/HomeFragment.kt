package com.example.nilme.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.nilme.Adapter.PostAdapter
import com.example.nilme.Model.Post
import com.example.nilme.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
   // TODO: Rename and change types of parameters

    private var postAdapter: PostAdapter? = null
    private var postList: MutableList<Post>? = null
    private var followingList: MutableList<Post>? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_home, container, false)

        var recycleView: RecyclerView? = null
        recycleView = view.findViewById(R.id.recycler_view_home)

        val linearLayoutManager =  LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recycleView.layoutManager = linearLayoutManager

        postList = ArrayList()
        postAdapter = context?.let { PostAdapter(it , postList as ArrayList<Post>) }
        recycleView.adapter = postAdapter

        checkFollowings()

        return view
    }

    private fun checkFollowings() {
        followingList = ArrayList()

        val followingRef =

                FirebaseDatabase.getInstance().reference

                        .child("Follow").child(FirebaseAuth.getInstance().currentUser!!.uid)

                        .child("Following")

        followingRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists())
                {
                    (followingList as ArrayList<String>).clear()

                    for(snapshot in p0.children)
                    {
                        snapshot.key?.let { (followingList as ArrayList<String>).add(it) }
                    }

                    readPosts()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun readPosts() {
        val postRef =

                FirebaseDatabase.getInstance().reference

                        .child("Posts")

        postRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                postList?.clear()

                for(snapshot in p0.children)
                {
                    val post = snapshot.getValue(Post::class.java)

                    for(id in (followingList as ArrayList<String>))
                    {
                        if(post!!.getPublisher() == id)
                        {
                            postList!!.add(post)
                        }

                        postAdapter!!.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


}