# FBCollageCover
Initial Commit


Input: 3 images. Taken either from the gallery or directly from the camera.
Output: Stitched image of these three images.It avoids stretching images. 

How it works:
- Asks user to input 3 Pics to choose from Gallery or Camera
- Restricts user to upload any pic with resolution below 300*300 to keep our output image in 	good quality.
- Takes three bitmaps to store images.. re-sized them.
- Merge three bitmaps. Created one big collage.
- facebook sdk is integrated.
- there are options to Save and Share (whatsapp, fb, others..)


Challenges you might face:

- Single BITMAP could be pretty large.. taking up all your heap memory and leave you with OOM ERROR.
so every bitmap needed to be stored and re-sized with care (for minimum memory use)

- Using static references to the bitmap array. which need to be freed when the activity is not using them TO AVOID MEMORY LEAKS.

- IMAGE COMING FROM CAMERA is a bit difficult to handle and finally set
there we have an 'EXIF flag' for orientation which needs to checked before re-sizing the image and setting the view in activity. 

