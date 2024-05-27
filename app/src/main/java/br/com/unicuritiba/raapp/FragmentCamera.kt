package br.com.unicuritiba.raapp

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.Range
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class FragmentCamera : Fragment() {

    private var previewView : PreviewView? = null
    private var imageView : ImageView? = null

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
        imageView = view.findViewById(R.id.image_view_element)

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

        val imageAnalysis = getImageAnalysis()

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(requireContext())){ image ->
            analyzerImage(image)
        }
        cameraProvider.bindToLifecycle(
            viewLifecycleOwner,
            cameraSelector,
            preview,
            imageAnalysis)
    }

    private fun analyzerImage(image: ImageProxy){
        val bitmap = image.toBitmap()
        val width = image.width - 1
        val height = image.height - 1

        var currentRed = 0
        var currentX = 0
        var currentY = 0

        for(x in 0..width){
            for(y in 0..height){
                val pixel = bitmap.getPixel(x,y)
                val pixelColorRed = Color.red(pixel)
                if(pixelColorRed > currentRed){
                    currentRed = pixelColorRed
                    currentX = x
                    currentY = y
                }
            }
        }
        setMarkerOnPosition(currentX,currentY,currentRed)
    }

    private fun setMarkerOnPosition(positionX: Int,
                                    positionY: Int,
                                    intensity: Int) {
        val layoutParams = imageView?.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.marginStart = positionX
        layoutParams.topMargin = positionY
        imageView?.layoutParams = layoutParams
    }

    private fun getImageAnalysis() : ImageAnalysis{
        return ImageAnalysis.Builder()
            .setTargetRotation(Surface.ROTATION_90)
            .build()
    }
}