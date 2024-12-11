import cv2 
import numpy as np
import os 


class ImageCrop():
    def __init__(self, config):
        '''
        open_path : original image open path
        save_path : crop image save path
        rotate_path : rotate image save path
        rotation_angle_circle : circle rotation angle
        rotation_angle_ellipse : ellipse rotation angle
        '''
        
        self.open_path = config['open_path']
        self.save_path = config['save_path']
        self.rotate_path = config['rotate_path']
        self.rotate_angle = int(config['rotation_angle'])
        
        # Permission Error retry another path
        self.error_path_crop = './crop'
        self.error_path_rotation = './rotation'
        
        # dir index setting
        self.start_dir = config['start_dir_idx']
        self.end_dir = config['end_dir_idx']

        
    # square four point return 
    def ImageArea(self, input_image):
        
        rgba = cv2.medianBlur(input_image, 55)
    
        imgray = cv2.cvtColor(rgba, cv2.COLOR_BGRA2GRAY)
        contours, _ = cv2.findContours(imgray, cv2.RETR_TREE, cv2.CHAIN_APPROX_NONE)

        contours_t = contours[0].transpose()
    
        right_point_x = np.max(contours_t[0])
        left_point_x = np.min(contours_t[0])
        right_point_y = np.max(contours_t[1])
        left_point_y = np.min(contours_t[1])
    
        return left_point_x, right_point_x, left_point_y, right_point_y
    
    
    # image crop
    def CropShape(self, input_image):
        left_x, right_x, left_y, right_y = self.ImageArea(input_image)
        crop_img = input_image[left_y:right_y, left_x:right_x]
            
        return crop_img
        
        
    # circle image rotation
    def rotate_image(self, save_rotate_img, input_image):
        i = 0
        height, width, channel = input_image.shape
    
        while i < 360:
            f_path = save_rotate_img + '_' + str(i) + '.png'
            if not os.path.isfile(f_path):
                matrix = cv2.getRotationMatrix2D((width/2, height/2), i, 1)
                dst = cv2.warpAffine(input_image, matrix, (width, height))
                dst = self.CropShape(dst)
                
                ret, img = cv2.imencode('.png', dst)

                if ret:
                    with open(f_path, mode='w+b') as f: 
                        img.tofile(f)

            else:
                print('rotate file exits : ', f_path)
            
            i = i + self.rotate_angle
    
    
    # image crop and rotation process
    def ImageProcess(self, shape):
        or_dirnames = os.listdir(self.open_path)

        if( int(self.start_dir) == -1 ):
            dirnames = or_dirnames
        else:
            dirnames = or_dirnames[int(self.start_dir):int(self.end_dir)]

        for dir in dirnames:
            open_folder_path = self.open_path + dir
            save_folder_path = self.save_path + dir
            rotate_folder_path = self.rotate_path + dir

            os.makedirs(save_folder_path, exist_ok=True)
            os.makedirs(rotate_folder_path, exist_ok=True)

            for path, folder, files in os.walk(open_folder_path):
                for file in files:
                    input_image = open_folder_path + '/' + file
                    save_image = save_folder_path + '/' + file[0:len(file)-3] + 'png'
                    
                    input_image = cv2.imdecode(np.fromfile(input_image, dtype=np.uint8), cv2.IMREAD_COLOR)

                    '''image crop'''
                    if not os.path.isfile(save_image):
                        crop_img = self.CropShape(input_image)
                        ret, img = cv2.imencode('.jpg', crop_img)

                        if ret:
                            with open(save_image, mode='w+b') as f: 
                                img.tofile(f)

                    else:
                        print( 'crop image file exits : ', save_image)

                    '''rotation'''
                    save_rotate_img = rotate_folder_path + '/' + file[0:len(file)-4]
                    self.rotate_image(save_rotate_img, input_image)

                    print(f'Crop & Rotate : {save_image}')







