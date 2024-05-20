package br.com.unicuritiba.raapp

import android.os.Bundle
import android.util.Range
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import java.util.concurrent.Executors

class FragmentCamera : Fragment() {

    private var previewView : PreviewView? = null
    private var camera : Camera? = null
    private val cameraExecutor = Executors.newSingleThreadExecutor()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(
            R.layout.fragment_camera,
            container,
            false
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        previewView = view.findViewById(R.id.preview_camera)
        previewView?.post {
            configureCamera()
        }
    }

    private fun configureCamera(){

        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider =  cameraProviderFuture.get()
            configurePreview(cameraProvider)
        },ContextCompat.getMainExecutor(requireContext()))
    }

    private fun configurePreview(cameraProvider: ProcessCameraProvider){

        val preview = Preview.Builder()
            .setTargetFrameRate(Range(30,60))
            .build()

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

        preview.setSurfaceProvider(previewView?.surfaceProvider)
        cameraProvider.bindToLifecycle(viewLifecycleOwner,cameraSelector,preview)
    }
}