import math


# Calculates the distance between the points (x1,y1) and (x2,y2).
# @return distance
def distance(x1, y1, x2, y2):
    return math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1))


# Calculates the angle of a vector from (x1,y1) to (x2,y2) relative to the x-axis.
# Angles are measured from x-axis in counter-clockwise direction and between 0 and 360
# @return angle in degrees
def angle(x1, y1, x2, y2):
    angle_value = math.degrees(math.atan2(y2 - y1, x2 - x1))
    if angle_value < 0:
        angle_value += 360
    return angle_value


def angle_between_unit_and_point_using_parameters(unit_x, unit_y, unit_orientation_angle, point_x, point_y):
    unit_to_point_angle = angle(unit_x, unit_y, point_x, point_y)

    angle_value = unit_to_point_angle - unit_orientation_angle

    if angle_value > 180:
        angle_value -= 360
    elif angle_value < -180:
        angle_value += 360

    return angle_value


# Returns an angle between where is unit looking at (it's orientation) and the specified point.
# If the angle is 0, unit looks directly at a point, if angle is negative the unit looks to the left
# side of the point and needs to rotate right to decrease the angle and if the angle is positive the
# unit looks to the right side of the point and it needs to turn left to look closer to the point.
# @return angle in degrees between -180 to 180 degrees
def angle_between_unit_and_point(unit, x, y):
    return angle_between_unit_and_point_using_parameters(unit["x"], unit["y"], unit["orientationAngle"], x, y)
